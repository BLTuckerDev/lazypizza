# Android Development Style Guide

This document defines the conventions and patterns to follow when writing Android applications. Follow these rules to maintain consistency with my preferred development style.

## Project Architecture

### Package Organization
Organize code by **feature**, not by layer. Each feature gets its own package containing all related components:

```
app/src/main/java/dev/bltucker/{appname}/
├── common/                          # Shared utilities, theme, composables, repositories
│   ├── composables/                 # Reusable UI components
│   ├── di/                          # Dependency injection modules (or use hilt/)
│   ├── repositories/                # Data repositories (or place alongside related code)
│   ├── room/                        # Database entities, DAOs, database class, RoomModule
│   └── theme/                       # Colors, Typography, Theme
├── {feature}/                       # Feature package (e.g., dashboard, login, settings)
│   ├── {Feature}Screen.kt           # Screen composable + NavGraphBuilder extension
│   ├── {Feature}ScreenModel.kt      # UI state data class + ModelReducer
│   ├── {Feature}ScreenViewModel.kt  # ViewModel
│   └── composables/                 # Feature-specific composables (optional)
```

### Navigation Pattern

#### Route Constants and Navigation Extensions
Define route constants and navigation helper functions in the screen file:

```kotlin
const val FEATURE_SCREEN_ROUTE = "feature"
const val FEATURE_ID_ARG = "featureId"

fun createFeatureScreenRoute(featureId: String): String {
    return "feature/$featureId"
}

fun NavController.navigateToFeature(featureId: Long? = null) {
    val route = buildString {
        append(FEATURE_SCREEN_ROUTE)
        if (featureId != null) {
            append("?featureId=$featureId")
        }
    }
    navigate(route)
}

fun NavGraphBuilder.featureScreen(
    onNavigateBack: () -> Unit,
    onNavigateToNext: (Long) -> Unit
) {
    composable(
        route = "$FEATURE_SCREEN_ROUTE?featureId={featureId}",
        arguments = listOf(
            navArgument("featureId") {
                type = NavType.LongType
                defaultValue = -1L
            }
        )
    ) { backStackEntry ->
        // Screen implementation
    }
}
```

#### Simple Route Helper Functions
For screens with required path parameters, use a simple helper function:

```kotlin
const val DETAIL_SCREEN_ROUTE = "detail/{photoId}"
const val PHOTO_ID_ARG = "photoId"

fun createDetailScreenRoute(photoId: String): String {
    return "detail/$photoId"
}
```

#### Deep Links
For screens that need deep link support (e.g., alarm triggers, notifications):

```kotlin
fun NavGraphBuilder.featureScreen(onDismiss: () -> Unit) {
    composable(
        route = "$FEATURE_ROUTE/{$FEATURE_ID_ARG}",
        arguments = listOf(
            navArgument(FEATURE_ID_ARG) {
                type = NavType.LongType
            }
        ),
        deepLinks = listOf(
            navDeepLink {
                uriPattern = "appname://feature/{$FEATURE_ID_ARG}"
                action = Intent.ACTION_VIEW
            }
        )
    ) {
        // Screen implementation
    }
}
```

#### Passing Data Back via SavedStateHandle
For returning results from a screen (e.g., selection screens):

```kotlin
const val SELECTION_REQUEST_KEY = "selection_result"

// In the destination screen
fun NavGraphBuilder.selectionScreen(
    onNavigateBack: (String) -> Unit
) {
    composable(...) {
        // When selection is made:
        onNavigateBack(selectedValue)
    }
}

// In NavigationGraph
selectionScreen(
    onNavigateBack = { result ->
        navController.previousBackStackEntry?.savedStateHandle?.set(SELECTION_REQUEST_KEY, result)
        navController.popBackStack()
    }
)

// In the calling screen, observe the result
val result by backStackEntry.savedStateHandle.getStateFlow<String?>(SELECTION_REQUEST_KEY, null).collectAsStateWithLifecycle()

LaunchedEffect(result) {
    result?.let { value ->
        viewModel.onResultReceived(value)
        backStackEntry.savedStateHandle.remove<String>(SELECTION_REQUEST_KEY)
    }
}
```

### Navigation Graph Structure
Create a central navigation graph composable:

```kotlin
@Composable
fun AppNavigationGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = HOME_ROUTE
    ) {
        homeScreen(
            onNavigateToDetail = { id ->
                navController.navigate(createDetailScreenRoute(id))
            }
        )
        
        detailScreen(
            onNavigateBack = {
                navController.popBackStack()
            }
        )
    }
}
```

## ViewModel Pattern

### Structure
Every screen has a dedicated ViewModel following this pattern:

```kotlin
@HiltViewModel
class FeatureScreenViewModel @Inject constructor(
    private val repository: SomeRepository,
    private val modelReducer: FeatureScreenModelReducer,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val featureId: Long? = savedStateHandle.get<Long>(FEATURE_ID_ARG)?.takeIf { it != -1L }

    @VisibleForTesting
    val mutableModel = MutableStateFlow(modelReducer.createInitialState())
    val observableModel: StateFlow<FeatureScreenModel> = mutableModel

    private var hasStarted = false

    fun onStart() {
        if (hasStarted) {
            return
        }
        hasStarted = true
        
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            try {
                val data = repository.getData(featureId)
                mutableModel.update {
                    modelReducer.updateWithData(it, data)
                }
            } catch (e: Exception) {
                mutableModel.update {
                    modelReducer.updateWithError(it)
                }
            }
        }
    }

    fun onSomeAction() {
        mutableModel.update {
            modelReducer.updateSomeField(it, newValue)
        }
    }
}
```

### Key ViewModel Conventions
- Use `hasStarted` flag to prevent re-initialization on configuration changes
- Name the state flow `observableModel` and private backing field `mutableModel`
- Annotate `mutableModel` with `@VisibleForTesting` for test access
- Use `update` extension for state modifications
- Prefix action handlers with `on` (e.g., `onButtonClick`, `onItemSelected`)
- Handle loading and error states explicitly
- Use `viewModelScope.launch` for coroutines
- Extract navigation arguments from `SavedStateHandle` in the ViewModel, not the screen
- Use `-1L` as default for optional Long arguments, then convert to null with `.takeIf { it != -1L }`
- Inject `ModelReducer` for state transitions (see ModelReducer Pattern below)

### Lifecycle-Aware Resource Management
For ViewModels that manage resources (media players, vibrators, etc.):

```kotlin
@HiltViewModel
class FeatureViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val vibrator: Vibrator,
    private val audioManager: AudioManager
) : ViewModel() {

    private var mediaPlayer: MediaPlayer? = null

    private fun startPlayback(uri: String, volume: Int) {
        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(context, Uri.parse(uri))
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                isLooping = true
                prepare()
                start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stopPlayback() {
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
            }
            release()
        }
        mediaPlayer = null
    }

    override fun onCleared() {
        super.onCleared()
        stopPlayback()
        vibrator.cancel()
    }
}
```

## Screen Model Pattern

### Structure
Screen models are immutable data classes representing UI state. Place the `ModelReducer` in the same file:

```kotlin
data class FeatureScreenModel(
    val featureId: Long? = null,
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val data: List<Item> = emptyList(),
    val selectedItemId: Long? = null,
    val showDialog: Boolean = false,
    val showBottomSheet: Boolean = false,
    val isSaveEnabled: Boolean = false
) {
    fun getItemById(itemId: Long): Item? {
        return data.find { it.id == itemId }
    }

    // Computed properties for derived state
    val hasUnsavedChanges: Boolean
        get() = // derived logic
}

class FeatureScreenModelReducer @Inject constructor() {

    fun createInitialState() = FeatureScreenModel(isLoading = true)

    fun updateWithData(previousModel: FeatureScreenModel, data: List<Item>): FeatureScreenModel {
        return previousModel.copy(
            data = data,
            isLoading = false,
            isError = false
        )
    }

    fun updateWithError(previousModel: FeatureScreenModel): FeatureScreenModel {
        return previousModel.copy(
            isLoading = false,
            isError = true
        )
    }

    fun updateSelectedItem(previousModel: FeatureScreenModel, itemId: Long?): FeatureScreenModel {
        return previousModel.copy(selectedItemId = itemId)
    }
}
```

### Key Model Conventions
- Provide sensible defaults for all properties
- Use `isLoading` and `isError` for async operation states
- Use `show*` prefix for UI visibility states (e.g., `showBottomSheet`, `showDialog`)
- Use `is*Enabled` for action enablement states (e.g., `isSaveEnabled`)
- Include helper methods for data access (e.g., `getItemById`)
- Keep models focused on UI state, not business logic

### ModelReducer Pattern
Use a dedicated reducer class for state transitions:

```kotlin
class FeatureScreenModelReducer @Inject constructor() {

    fun createInitialState() = FeatureScreenModel(isLoading = true)

    fun updateWithPhotos(previousModel: FeatureScreenModel, photos: List<Photo>): FeatureScreenModel {
        return previousModel.copy(
            photos = (previousModel.photos + photos).distinctBy { it.id },
            isLoading = false,
            isError = false,
            isPaging = false,
            nextPage = previousModel.nextPage + 1
        )
    }

    fun updateWithFatalError(previousModel: FeatureScreenModel): FeatureScreenModel {
        return previousModel.copy(
            isLoading = false,
            isError = true,
            isPaging = false
        )
    }

    fun updateWithPaging(previousModel: FeatureScreenModel, isPaging: Boolean): FeatureScreenModel {
        return previousModel.copy(isPaging = isPaging)
    }

    fun updateSearchTerm(previousModel: FeatureScreenModel, searchTerm: String): FeatureScreenModel {
        return previousModel.copy(searchTerm = searchTerm)
    }

    fun updateWithNewSearchResults(previousModel: FeatureScreenModel, photos: List<Photo>): FeatureScreenModel {
        return previousModel.copy(
            photos = photos.distinctBy { it.id },
            isLoading = false,
            isError = false,
            isPaging = false,
            nextPage = 2
        )
    }

    fun updateStateWithIsSearching(previousModel: FeatureScreenModel): FeatureScreenModel {
        return previousModel.copy(
            isSearching = true,
            isError = false,
            isPaging = false
        )
    }
}
```

#### Key Reducer Conventions
- Use `@Inject constructor()` for Hilt injection
- Name methods descriptively: `updateWith{State}`, `updateStateWith{Condition}`
- Always take `previousModel` as first parameter
- Return new model instance via `copy()`
- Handle deduplication in reducer (e.g., `.distinctBy { it.id }`)
- Include `createInitialState()` factory method
- Keep reducers as pure functions with no side effects

## Screen Composable Pattern

### Structure with ScreenActions

For screens with multiple callbacks, use a private `ScreenActions` data class:

```kotlin
const val FEATURE_SCREEN_ROUTE = "feature"

private data class ScreenActions(
    val onClickItem: (String) -> Unit,
    val onPage: () -> Unit,
    val onSearchTextChange: (String) -> Unit,
    val onPerformSearch: () -> Unit,
)

fun NavGraphBuilder.featureScreen(onClickItem: (String) -> Unit) {
    composable(route = FEATURE_SCREEN_ROUTE) {
        val viewModel = hiltViewModel<FeatureScreenViewModel>()
        val model by viewModel.observableModel.collectAsStateWithLifecycle()

        val screenActions = remember(viewModel) {
            ScreenActions(
                onPage = viewModel::onPageForMore,
                onClickItem = onClickItem,
                onSearchTextChange = viewModel::onSearchTextChange,
                onPerformSearch = viewModel::onPerformSearch
            )
        }

        LifecycleStartEffect(Unit) {
            viewModel.onStart()
            onStopOrDispose { }
        }

        FeatureScreen(
            modifier = Modifier.fillMaxSize(),
            model = model,
            screenActions = screenActions
        )
    }
}

@Composable
private fun FeatureScreen(
    modifier: Modifier = Modifier,
    model: FeatureScreenModel,
    screenActions: ScreenActions,
) {
    // Screen implementation
}
```

### Structure with Direct Callbacks

For simpler screens, pass callbacks directly:

```kotlin
fun NavGraphBuilder.featureScreen(
    onNavigateBack: () -> Unit,
    onNavigateToNext: (Long) -> Unit
) {
    composable(
        route = "$FEATURE_SCREEN_ROUTE?featureId={featureId}",
        arguments = listOf(
            navArgument("featureId") {
                type = NavType.LongType
                defaultValue = -1L
            }
        )
    ) { backStackEntry ->
        val viewModel = hiltViewModel<FeatureScreenViewModel>()
        val model by viewModel.observableModel.collectAsStateWithLifecycle()

        LifecycleStartEffect(Unit) {
            viewModel.onStart()
            onStopOrDispose { }
        }

        FeatureScreen(
            modifier = Modifier.fillMaxSize(),
            model = model,
            onBackClick = onNavigateBack,
            onItemClick = viewModel::onItemClick,
            onSave = {
                viewModel.onSave()
                onNavigateBack()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FeatureScreen(
    modifier: Modifier = Modifier,
    model: FeatureScreenModel,
    onBackClick: () -> Unit,
    onItemClick: (Long) -> Unit,
    onSave: () -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = onSave,
                        enabled = model.isSaveEnabled
                    ) {
                        Text("Save")
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            model.isLoading -> LoadingScreen(modifier = Modifier.fillMaxSize().padding(paddingValues))
            model.isError -> ErrorScreen(modifier = Modifier.fillMaxSize().padding(paddingValues), errorMessage = "Error loading data")
            else -> {
                FeatureContent(
                    modifier = Modifier.padding(paddingValues),
                    model = model,
                    onItemClick = onItemClick
                )
            }
        }
        
        if (model.showDialog) {
            FeatureDialog(
                // dialog props
            )
        }
    }
}
```

### Key Screen Conventions
- Use `LifecycleStartEffect` for ViewModel initialization (not `LaunchedEffect(Unit)`)
- Use `LifecycleResumeEffect` when you need to re-check state on resume (e.g., permissions)
- Use `ScreenActions` data class for screens with 4+ callbacks, direct params for simpler screens
- Make `ScreenActions` data class private within the file
- Use `remember(viewModel)` when creating `ScreenActions` to avoid recreation
- Separate navigation logic (in NavGraphBuilder extension) from UI (in Screen composable)
- Make screen composables private when only used via NavGraphBuilder extension
- Handle loading/error states with dedicated composables
- Always pass `Modifier` as first parameter to composables
- Extract complex UI sections into private composable functions
- Show dialogs conditionally based on model state inside the Scaffold

## Dependency Injection (Hilt)

### Dispatcher Module
Create qualifier annotations and provide dispatchers for testability:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DispatchersModule {

    @Provides
    @IO
    @Singleton
    fun provideIODispatcher(): CoroutineDispatcher {
        return Dispatchers.IO
    }

    @Provides
    @UI
    @Singleton
    fun provideUIDispatcher(): CoroutineDispatcher {
        return Dispatchers.Main
    }
}

@Qualifier
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class IO

@Qualifier
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class UI
```

### Module Organization
Place related modules together. System services can have their own module:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object SystemServicesModule {

    @Provides
    fun provideAlarmManager(@ApplicationContext context: Context): AlarmManager {
        return context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    @Provides
    fun provideVibrator(@ApplicationContext context: Context): Vibrator {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    @Provides
    fun provideAudioManager(@ApplicationContext context: Context): AudioManager {
        return context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }
}
```

### Room Module

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "appname.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideFeatureDao(database: AppDatabase): FeatureDao {
        return database.featureDao()
    }
}
```

### API/Network Module

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object FlickrModule {

    @Provides
    @Singleton
    fun provideFlickr(): Flickr {
        return Flickr(BuildConfig.FLICKR_API_KEY)
    }

    @Provides
    @Singleton
    fun provideFlickrPhotosInterface(flickr: Flickr): PhotosInterface {
        return flickr.photosInterface
    }
}
```

### Key DI Conventions
- Place modules in `common/di/` package or alongside related code (e.g., `RoomModule` in `common/room/`)
- Use `@Singleton` for repositories, database instances, and API clients
- Provide DAOs individually from the database
- Use `@Reusable` for stateless utility classes like intent factories
- Use `Provider<T>` when you need lazy initialization of dependencies
- Handle SDK version differences within providers
- Create dispatcher qualifiers (`@IO`, `@UI`) for testable coroutine code

## Repository Pattern

```kotlin
@Singleton
class FeatureRepository @Inject constructor(
    private val featureDao: FeatureDao,
    private val scheduler: FeatureScheduler,
    @IO private val coroutineDispatcher: CoroutineDispatcher
) {
    suspend fun getPagedItems(tag: String, page: Int = 1): Result<List<Item>> = runCatching {
        return withContext(coroutineDispatcher) {
            require(tag.isNotBlank()) { "Tag cannot be blank" }
            
            Result.success(featureDao.getItems(tag, page)
                .filter { !it.id.isNullOrEmpty() }
                .map { entity ->
                    Item(
                        id = entity.id,
                        title = entity.title ?: "Untitled"
                    )
                })
        }
    }

    suspend fun getItemById(id: String): Result<ItemDetail> = runCatching {
        return withContext(coroutineDispatcher) {
            val entity = featureDao.getItemById(id)
            Result.success(ItemDetail(
                id = entity.id,
                title = entity.title,
                description = entity.description
            ))
        }
    }

    fun observeAllItems(): Flow<List<FeatureEntity>> = featureDao.observeAllItems()

    suspend fun createItem(
        name: String,
        value: Int,
        isEnabled: Boolean = true
    ): Result<Long> = runCatching {
        return withContext(coroutineDispatcher) {
            require(value in 0..100) { "Value must be between 0 and 100" }

            val nextTime = calculateNextTime(value)

            val entity = FeatureEntity(
                name = name,
                value = value,
                isEnabled = isEnabled,
                nextScheduledTime = nextTime
            )

            val itemId = featureDao.insert(entity)
            
            if (isEnabled) {
                scheduler.schedule(entity.copy(id = itemId))
            }

            Result.success(itemId)
        }
    }

    companion object {
        const val DURATION_MS = 5 * 60 * 1000L
        const val DAY_IN_MILLIS = 24 * 60 * 60 * 1000L
    }
}
```

### Key Repository Conventions
- Inject `@IO` dispatcher and wrap operations in `withContext(coroutineDispatcher)`
- Use `require` for input validation
- Coordinate between DAO and scheduler/system services
- Define time constants as companion object properties
- Return the created ID from create operations
- Use `Flow` for observable queries
- Filter and map data transformations within the dispatcher context
- Mark with `@Reusable` scope unless they contain in memory state, in which case use `@Singleton`
- All methods are `suspend` functions
- Return `Result<T>` type (Kotlin stdlib)
- Wrap all logic in `runCatching { }` block

## Room Database

### Entity Pattern

```kotlin
@Entity(tableName = "features")
data class FeatureEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String?,
    val hour: Int,
    val minute: Int,
    val isEnabled: Boolean,

    val repeatDays: Long = 0,
    val setting: String = "default",
    val volume: Int = 50,

    val snoozedUntil: Long? = null,
    val nextScheduledTime: Long
) {
    fun isRepeating(): Boolean = repeatDays != 0L

    fun hasFlag(flag: Long): Boolean {
        return (repeatDays and flag) != 0L
    }
}
```

### DAO Pattern

```kotlin
@Dao
interface FeatureDao {
    @Query("SELECT * FROM features ORDER BY nextScheduledTime ASC")
    fun observeAllFeatures(): Flow<List<FeatureEntity>>

    @Query("SELECT * FROM features WHERE id = :featureId")
    suspend fun getFeatureById(featureId: Long): FeatureEntity?

    @Query("SELECT * FROM features WHERE isEnabled = 1")
    suspend fun getEnabledFeatures(): List<FeatureEntity>

    @Insert
    suspend fun insert(feature: FeatureEntity): Long

    @Update
    suspend fun update(feature: FeatureEntity)

    @Delete
    suspend fun delete(feature: FeatureEntity)

    @Query("UPDATE features SET isEnabled = :isEnabled WHERE id = :featureId")
    suspend fun updateFeatureEnabled(featureId: Long, isEnabled: Boolean)

    @Query("UPDATE features SET snoozedUntil = :snoozedUntil, nextScheduledTime = :nextScheduledTime WHERE id = :featureId")
    suspend fun updateFeatureSnooze(featureId: Long, snoozedUntil: Long?, nextScheduledTime: Long)
}
```

### Key Database Conventions
- Use simple column names without `@ColumnInfo` when the property name matches desired column name
- Return `Flow` for observable queries with `observe` prefix
- Use suspend functions for one-shot queries with `get` prefix
- Include targeted update queries for specific fields to avoid full entity updates
- Add helper methods to entities for computed properties

## Broadcast Receivers

### Structure

```kotlin
@AndroidEntryPoint
class FeatureReceiver : BroadcastReceiver() {

    @Inject
    lateinit var permissionChecker: PermissionChecker

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != EXPECTED_ACTION) {
            return
        }

        val featureId = intent.getLongExtra(EXTRA_FEATURE_ID, -1L)
        if (featureId == -1L) return

        if (permissionChecker.shouldUseForegroundService()) {
            val serviceIntent = Intent(context, FeatureService::class.java).apply {
                putExtra(FeatureService.EXTRA_FEATURE_ID, featureId)
            }
            context.startForegroundService(serviceIntent)
        } else {
            val activityIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("appname://feature/$featureId")
            ).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            context.startActivity(activityIntent)
        }
    }
}
```

### Intent Factory Pattern

```kotlin
@Reusable
class FeatureIntentFactory @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun createReceiverIntent(entity: FeatureEntity): Intent {
        return Intent(context, FeatureReceiver::class.java).apply {
            action = FEATURE_ACTION
            putExtra(EXTRA_FEATURE_ID, entity.id)
        }
    }

    fun createReceiverPendingIntent(entity: FeatureEntity): PendingIntent {
        return PendingIntent.getBroadcast(
            context,
            entity.id.toInt(),
            createReceiverIntent(entity),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    companion object {
        const val FEATURE_ACTION = "dev.bltucker.appname.FEATURE_ACTION"
        const val EXTRA_FEATURE_ID = "feature_id"
    }
}
```

## Foreground Services

### Structure

```kotlin
@AndroidEntryPoint
class FeatureService : Service() {

    @Inject
    lateinit var powerManager: PowerManager

    @Inject
    lateinit var repository: FeatureRepository

    private var wakeLock: PowerManager.WakeLock? = null
    private var mediaPlayer: MediaPlayer? = null
    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val featureId = intent?.getLongExtra(EXTRA_FEATURE_ID, -1L) ?: -1L
        if (featureId == -1L) {
            stopSelf()
            return START_NOT_STICKY
        }

        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "AppName::FeatureWakeLock"
        ).apply {
            acquire(10 * 60 * 1000L)
        }

        serviceScope.launch {
            val feature = repository.getFeatureById(featureId)
            if (feature == null) {
                stopSelf()
                return@launch
            }

            val notification = buildNotification(feature)
            startForeground(NOTIFICATION_ID, notification)
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        cleanup()
    }

    private fun cleanup() {
        mediaPlayer?.apply {
            if (isPlaying) stop()
            release()
        }
        mediaPlayer = null
        wakeLock?.let { if (it.isHeld) it.release() }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Feature Notifications",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            enableLights(true)
            enableVibration(true)
            setBypassDnd(true)
            lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        private const val CHANNEL_ID = "feature_service_channel"
        private const val NOTIFICATION_ID = 1
        const val EXTRA_FEATURE_ID = "feature_id"

        fun stopService(context: Context) {
            context.stopService(Intent(context, FeatureService::class.java))
        }
    }
}
```

## WorkManager

### HiltWorker Pattern

```kotlin
@HiltWorker
class RescheduleWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: FeatureRepository,
    private val scheduler: FeatureScheduler
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        private const val TAG = "RescheduleWorker"
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Starting work")

            val enabledItems = repository.getEnabledItems()

            enabledItems.forEach { item ->
                try {
                    processItem(item)
                } catch (e: Exception) {
                    Log.e(TAG, "Error processing item ${item.id}", e)
                }
            }

            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error during work", e)
            Result.retry()
        }
    }
}
```

### Enqueueing Work

```kotlin
val workManager = WorkManager.getInstance(context)

val work = OneTimeWorkRequestBuilder<RescheduleWorker>()
    .setBackoffCriteria(
        BackoffPolicy.EXPONENTIAL,
        WorkRequest.MIN_BACKOFF_MILLIS,
        TimeUnit.MILLISECONDS
    )
    .build()

workManager.enqueueUniqueWork(
    "unique_work_name",
    ExistingWorkPolicy.REPLACE,
    work
)
```

## Bitmask Utilities

For storing multiple boolean flags efficiently (e.g., days of week):

```kotlin
object FeatureFlags {
    private const val FLAG_A = 1L shl 0
    private const val FLAG_B = 1L shl 1
    private const val FLAG_C = 1L shl 2

    fun fromList(flags: List<FlagType>): Long {
        var bitmask = 0L
        flags.forEach { flag ->
            bitmask = bitmask or when (flag) {
                FlagType.A -> FLAG_A
                FlagType.B -> FLAG_B
                FlagType.C -> FLAG_C
            }
        }
        return bitmask
    }

    fun toList(bitmask: Long): List<FlagType> {
        val flags = mutableListOf<FlagType>()
        if (bitmask and FLAG_A != 0L) flags.add(FlagType.A)
        if (bitmask and FLAG_B != 0L) flags.add(FlagType.B)
        if (bitmask and FLAG_C != 0L) flags.add(FlagType.C)
        return flags
    }

    fun isEnabled(bitmask: Long, flag: FlagType): Boolean {
        val bitFlag = when (flag) {
            FlagType.A -> FLAG_A
            FlagType.B -> FLAG_B
            FlagType.C -> FLAG_C
        }
        return bitmask and bitFlag != 0L
    }
}
```

## Compose UI Conventions

### Theme Usage
- Define colors in `Color.kt`, typography in `Type.kt`
- Use `MaterialTheme.colorScheme.*` for colors
- Use `MaterialTheme.typography.*` for text styles
- Create custom color values for app-specific branding
- Define both light and dark color schemes

### Custom Colors Example

```kotlin
val MoonBlue = Color(0xFF0F3460)
val DeepSpace = Color(0xFF1A1A2E)
val MoonSurface = Color(0xFFE2E2E2)
val MoonCrater = Color(0xFFC4C4C4)
val MoonHighlight = Color(0xFFD6D6D6)
val MoonShadow = Color(0xFF16213E)

val AccentBlue = Color(0xFF3282B8)
val AccentLightBlue = Color(0xFFBBE1FA)
val AccentDeepBlue = Color(0xFF0F4C75)
```

### Theme with Light/Dark Support

```kotlin
private val LightColors = lightColorScheme(
    primary = AccentBlue,
    onPrimary = Color.White,
    primaryContainer = AccentLightBlue,
    onPrimaryContainer = AccentDeepBlue,
    secondary = MoonBlue,
    onSecondary = Color.White,
    background = MoonSurface,
    onBackground = DeepSpace,
    surface = MoonHighlight,
    onSurface = DeepSpace,
    surfaceVariant = MoonCrater,
    onSurfaceVariant = MoonShadow,
    error = Color(0xFFB00020),
    onError = Color.White
)

private val DarkColors = darkColorScheme(
    primary = AccentLightBlue,
    onPrimary = AccentDeepBlue,
    primaryContainer = AccentBlue,
    onPrimaryContainer = Color.White,
    secondary = MoonHighlight,
    onSecondary = MoonBlue,
    background = DeepSpace,
    onBackground = MoonHighlight,
    surface = MoonShadow,
    onSurface = MoonHighlight,
    surfaceVariant = DeepSpace.copy(alpha = 0.7f),
    onSurfaceVariant = MoonCrater,
    error = Color(0xFFCF6679),
    onError = Color.Black
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
```

### Typography with Custom Fonts

```kotlin
private val CustomFont = FontFamily(
    Font(R.font.custom_regular),
    Font(R.font.custom_medium, FontWeight.Medium),
    Font(R.font.custom_semibold, FontWeight.SemiBold),
    Font(R.font.custom_bold, FontWeight.Bold)
)

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = CustomFont,
        fontWeight = FontWeight.Normal,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)
```

### Common Composables
Create reusable composables in `common/composables/`:

```kotlin
@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorScreen(
    modifier: Modifier = Modifier,
    errorMessage: String
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(errorMessage)
    }
}
```

@Composable
fun DetailImageView(
    modifier: Modifier = Modifier,
    photo: DetailPhoto
) {
    var isLoading by remember { mutableStateOf(true) }

    val aspectRatio = remember(photo.id) {
        if (photo.originalWidth != null && photo.originalHeight != null &&
            photo.originalWidth > 0 && photo.originalHeight > 0
        ) {
            photo.originalWidth.toFloat() / photo.originalHeight.toFloat()
        } else {
            4f / 3f
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(aspectRatio),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxSize()
                .placeholder(
                    visible = isLoading,
                    highlight = PlaceholderHighlight.shimmer()
                ),
            model = ImageRequest.Builder(LocalContext.current)
                .data(photo.imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = photo.title,
            contentScale = ContentScale.Crop,
            error = painterResource(R.drawable.placeholder),
            onLoading = { isLoading = true },
            onSuccess = { isLoading = false },
            onError = { isLoading = false }
        )
    }
}
```

### Infinite Scroll / Pagination Handler

```kotlin
@Composable
private fun LoadMoreHandler(
    listState: LazyGridState,
    model: FeatureScreenModel,
    buffer: Int = 5,
    onLoadMore: () -> Unit
) {
    val shouldLoadMore by remember(listState, model.isPaging, model.items.size) {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount

            if (totalItems == 0 || model.isPaging) {
                false
            } else {
                val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
                lastVisibleItemIndex >= totalItems - 1 - buffer
            }
        }
    }

    LaunchedEffect(shouldLoadMore) {
        snapshotFlow { shouldLoadMore }
            .filter { it }
            .collect {
                onLoadMore()
            }
    }
}
```

### Search Bar in TopAppBar

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchScreen(
    modifier: Modifier = Modifier,
    screenActions: ScreenActions,
    model: SearchScreenModel
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    OutlinedTextField(
                        value = model.searchTerm,
                        onValueChange = screenActions.onSearchTextChange,
                        modifier = Modifier.fillMaxWidth().padding(end = 8.dp),
                        placeholder = { Text("Search...") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Search
                        ),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                screenActions.onPerformSearch()
                                keyboardController?.hide()
                            }
                        ),
                        trailingIcon = {
                            IconButton(onClick = {
                                screenActions.onPerformSearch()
                                keyboardController?.hide()
                            }) {
                                Icon(Icons.Filled.Search, contentDescription = "Search")
                            }
                        }
                    )
                }
            )
        }
    ) { paddingValues ->
        // Content
    }
}
```

### Spacing and Layout
- Use `Spacer` with specific heights/widths for layout spacing
- Use `Modifier.fillMaxSize()` for root content
- Use `Modifier.fillMaxWidth()` for full-width components
- Use `Arrangement.spacedBy()` in Column/Row for consistent spacing
- Chain modifiers in order: size → padding → appearance → interactions


## Dialog Pattern

```kotlin
@Composable
fun FeatureDialog(
    value: String,
    onValueChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Dialog Title",
                    style = MaterialTheme.typography.titleLarge
                )
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Enter value") }
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    TextButton(onClick = { onSave(value) }) {
                        Text("Save")
                    }
                }
            }
        }
    }
}
```

## Preview Pattern

```kotlin
@Preview(showBackground = true)
@Composable
private fun FeatureScreenPreview() {
    val previewData = listOf(
        FeatureEntity(id = 1, name = "Item 1", ...),
        FeatureEntity(id = 2, name = "Item 2", ...),
    )

    val previewModel = FeatureScreenModel(
        data = previewData,
        isLoading = false
    )

    AppTheme {
        FeatureScreen(
            model = previewModel,
            onBackClick = {},
            onItemClick = {},
            onSave = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyFeatureScreenPreview() {
    val emptyModel = FeatureScreenModel(
        data = emptyList(),
        isLoading = false
    )

    AppTheme {
        FeatureScreen(
            model = emptyModel,
            onBackClick = {},
            onItemClick = {},
            onSave = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoadingFeatureScreenPreview() {
    val loadingModel = FeatureScreenModel(
        isLoading = true
    )

    AppTheme {
        FeatureScreen(
            model = loadingModel,
            onBackClick = {},
            onItemClick = {},
            onSave = {}
        )
    }
}
```

## Permission Handling

### Permission Checker Utility

```kotlin
@Singleton
class PermissionChecker @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun needsNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        } else {
            false
        }
    }

    fun shouldUseForegroundService(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    }
}
```

### Permission Request in Screen

```kotlin
@Composable
fun FeatureListScreen(
    model: FeatureListScreenModel,
    onCheckPermission: () -> Unit
) {
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onCheckPermission()
        }
    }

    if (!model.hasPermission) {
        PermissionRequestContent(
            onRequestPermission = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            },
            onOpenSettings = {
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                }
                context.startActivity(intent)
            }
        )
    } else {
        // Main content
    }
}
```

### Permission Check in ViewModel

```kotlin
fun checkPermission() {
    val hasPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        true
    }

    mutableModel.update { it.copy(hasPermission = hasPermission) }
}
```

## MainActivity Pattern

```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var navController: NavHostController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val rememberedNavController = rememberNavController()
            navController = rememberedNavController
            AppTheme {
                AppNavigationGraph(rememberedNavController)
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        navController?.handleDeepLink(intent)
    }
}
```


## Naming Conventions

### Files
- `{Feature}Screen.kt` - Screen composable and navigation setup
- `{Feature}ScreenModel.kt` - UI state data class and ModelReducer
- `{Feature}ScreenViewModel.kt` - ViewModel
- `{Feature}Dao.kt` - Room DAO interface
- `{Feature}Entity.kt` - Room entity
- `{Feature}Repository.kt` - Data repository
- `{Feature}Receiver.kt` - Broadcast receiver
- `{Feature}Service.kt` - Foreground service
- `{Feature}Worker.kt` - WorkManager worker
- `{Feature}IntentFactory.kt` - PendingIntent creation
- `{Feature}Scheduler.kt` - AlarmManager scheduling

### Functions
- `on{Action}` - Event handlers in ViewModel (e.g., `onItemClick`, `onSaveClick`)
- `navigateTo{Feature}` - NavController extensions
- `create{Feature}Route` - Route creation helper functions
- `{feature}Screen` - NavGraphBuilder extensions
- `observe{Items}` - Flow-returning DAO methods
- `get{Item}` - Suspend DAO methods
- `updateWith{State}` - ModelReducer methods
- `updateStateWith{Condition}` - ModelReducer methods
- `createInitialState` - ModelReducer factory method

### Variables
- `mutableModel` / `observableModel` - ViewModel state
- `hasStarted` - Initialization guard
- `show{Element}` - UI visibility flags
- `is{State}` - Boolean state flags (e.g., `isLoading`, `isError`, `isEnabled`)
- `{FEATURE}_ROUTE` - Route constants
- `{FEATURE}_ARG` - Argument name constants
- `{FEATURE}_REQUEST_KEY` - SavedStateHandle keys

### Classes
- `{Feature}ScreenModelReducer` - State transition logic

## Error Handling

**Repository error handling:**
- Repositories return `Result<T>` wrapped in `runCatching { }`
- ViewModels use `.onSuccess { }` and `.onFailure { }` to handle results

**ViewModel result handling pattern:**
```kotlin
viewModelScope.launch {
    repository.getData()
        .onSuccess { data ->
            mutableModel.update {
                modelFactory.updateWithSuccess(it, data)
            }
        }
        .onFailure { error ->
            val message = resourcesProvider.getString(R.string.error_message)
            mutableModel.update {
                modelFactory.updateWithError(it, message)
            }
        }
}
```

## Testing Conventions

### Test Setup and Structure

```kotlin
@ExperimentalCoroutinesApi
class FeatureScreenViewModelTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var mockRepository: FeatureRepository

    private val realReducer: FeatureScreenModelReducer = FeatureScreenModelReducer()

    private lateinit var objectUnderTest: FeatureScreenViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        objectUnderTest = FeatureScreenViewModel(
            mockRepository,
            realReducer
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}
```

### Test Naming Convention
Use backticks with descriptive GIVEN/WHEN/THEN format:

```kotlin
@Test
fun `initial state is loading`() = runTest(testDispatcher) {
    assertEquals(initialState, objectUnderTest.mutableModel.value)
}

@Test
fun `onStart success loads data`() = runTest(testDispatcher) {
    val expectedState = realReducer.updateWithData(initialState, testData)

    coEvery { mockRepository.getData(testId) } returns testData

    objectUnderTest.onStart(testId)
    advanceUntilIdle()

    assertEquals(expectedState, objectUnderTest.mutableModel.value)
    coVerify(exactly = 1) { mockRepository.getData(testId) }
    confirmVerified(mockRepository)
}

@Test
fun `onStart failure updates state to error`() = runTest(testDispatcher) {
    val exception = RuntimeException("Network error")
    val expectedState = realReducer.updateWithError(initialState)

    coEvery { mockRepository.getData(testId) } throws exception

    objectUnderTest.onStart(testId)
    advanceUntilIdle()

    assertEquals(expectedState, objectUnderTest.mutableModel.value)
}

@Test
fun `onStart called multiple times only loads once`() = runTest(testDispatcher) {
    coEvery { mockRepository.getData(testId) } returns testData

    objectUnderTest.onStart(testId)
    advanceUntilIdle()

    objectUnderTest.onStart(testId)
    advanceUntilIdle()

    coVerify(exactly = 1) { mockRepository.getData(testId) }
}
```

### Testing ModelReducers Separately

```kotlin
class FeatureScreenModelReducerTest {

    private lateinit var objectUnderTest: FeatureScreenModelReducer

    @Before
    fun setUp() {
        objectUnderTest = FeatureScreenModelReducer()
    }

    @Test
    fun `createInitialState should return state with isLoading true`() {
        val initialState = objectUnderTest.createInitialState()

        assertTrue(initialState.isLoading)
        assertNull(initialState.data)
        assertFalse(initialState.isError)
    }

    @Test
    fun `updateWithData should update state with data and reset flags`() {
        val previousModel = FeatureScreenModel(isLoading = true, isError = true)
        val updatedModel = objectUnderTest.updateWithData(previousModel, testData)

        assertFalse(updatedModel.isLoading)
        assertFalse(updatedModel.isError)
        assertEquals(testData, updatedModel.data)
    }

    @Test
    fun `updatePhotos should handle duplicates based on id when appending`() {
        val existingPhoto = GalleryPhoto(id = "1", imageUrl = "url1", title = "photo 1")
        val previousModel = SearchScreenModel(
            galleryPhotos = listOf(existingPhoto),
            isPaging = true
        )

        val duplicatePhoto = GalleryPhoto(id = "1", imageUrl = "url1_new", title = "photo 1 duplicate")
        val newPhoto = GalleryPhoto(id = "2", imageUrl = "url2", title = "photo 2")
        val newPhotos = listOf(duplicatePhoto, newPhoto)

        val updatedModel = objectUnderTest.updatePhotos(previousModel, newPhotos)

        assertEquals(2, updatedModel.galleryPhotos.size)
        assertEquals(existingPhoto, updatedModel.galleryPhotos.find { it.id == "1" })
    }
}
```

### Testing Repository with Dispatcher

```kotlin
@ExperimentalCoroutinesApi
class FeatureRepositoryTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    private lateinit var mockDao: FeatureDao

    private lateinit var objectUnderTest: FeatureRepository

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        objectUnderTest = FeatureRepository(mockDao, testDispatcher)
    }

    @Test
    fun `getPagedItems WHEN api returns items THEN filters and maps correctly`() = runTest(testDispatcher) {
        val mockItems = listOf(validItem1, validItem2, invalidItem)
        coEvery { mockDao.getItems(any(), any()) } returns mockItems

        val result = objectUnderTest.getPagedItems("tag", 1)

        assertEquals(2, result.size)
        coVerify(exactly = 1) { mockDao.getItems("tag", 1) }
    }

    @Test
    fun `getPagedItems WHEN tag is blank THEN throws IllegalArgumentException`() = runTest(testDispatcher) {
        try {
            objectUnderTest.getPagedItems("", 1)
            fail("Expected IllegalArgumentException was not thrown")
        } catch (e: IllegalArgumentException) {
            assertEquals("Tag cannot be blank", e.message)
        }
        coVerify(exactly = 0) { mockDao.getItems(any(), any()) }
    }
}
```

### Key Testing Conventions
- Use MockK for mocking with `@MockK` annotation
- Use `MockKRule` instead of manual MockK initialization
- Use `StandardTestDispatcher` for coroutine testing
- Use real reducers in ViewModel tests, mock only repositories
- Test reducers in isolation with their own test class
- Use `advanceUntilIdle()` after async operations
- Use `coEvery` for suspend functions, `every` for regular functions
- Use `coVerify` and `confirmVerified` to verify interactions
- Name test variable `objectUnderTest` for clarity
- Use backticks for descriptive test names
- Enable `unitTests.isReturnDefaultValues = true` in gradle for Android framework stubs
- Use `relaxed = true` for mocks that don't need explicit stubbing
- Place tests in corresponding `test/` directory mirroring main source structure

## Build Configuration

### Key Gradle Settings
```kotlin
android {
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    
    kotlinOptions {
        jvmTarget = "17"
    }
    
    buildFeatures {
        compose = true
        buildConfig = true
    }
    
    testOptions {
        unitTests {
            isReturnDefaultValues = true
            isIncludeAndroidResources = true
        }
    }
}

kapt {
    correctErrorTypes = true
    useBuildCache = true
    arguments {
        arg("dagger.fastInit", "true")
        arg("dagger.hilt.android.internal.disableAndroidSuperclassValidation", "true")
        arg("kapt.kotlin.generated", "true")
    }
}
```

### Preferred Libraries
- **DI**: Hilt
- **Navigation**: Jetpack Navigation Compose
- **Database**: Room with KSP
- **State Management**: StateFlow with collectAsStateWithLifecycle
- **Async**: Kotlin Coroutines
- **Image Loading**: Coil (with coil3 and coil-network)
- **Background Work**: WorkManager with Hilt integration
- **Scheduling**: AlarmManager with AlarmClockInfo
- **Placeholder/Shimmer**: compose-placeholder-material3
- **Testing**: MockK, coroutines-test, arch-core-testing

## Code Style

- No inline comments in generated code
- Use `when` expressions for multiple conditions
- Prefer `copy()` for data class modifications (via ModelReducer)
- Use extension functions for cleaner APIs
- Keep composables focused and single-purpose
- Extract complex UI into separate private composable functions
- Use companion objects for constants
- Handle SDK version differences with explicit version checks
- Use `buildString` for complex string construction
- Make screen composables private when only accessed via NavGraphBuilder extension
- Use `@VisibleForTesting` for test-accessible internal state
