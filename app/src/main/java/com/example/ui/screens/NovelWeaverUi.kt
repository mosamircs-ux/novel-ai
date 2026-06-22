package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.Novel
import com.example.data.model.Character
import com.example.data.model.Event
import com.example.data.model.Dialogue
import com.example.viewmodel.NovelViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.parser.PdfTextExtractor
import java.io.InputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


// Bold Typography Design Theme Palette
val CinemaDarkBackground @Composable get() = MaterialTheme.colorScheme.background
val CinemaSurface @Composable get() = MaterialTheme.colorScheme.surface
val CinemaCardBackground @Composable get() = MaterialTheme.colorScheme.surfaceVariant
val CinemaGoldAccent @Composable get() = MaterialTheme.colorScheme.primary
val CinemaTealAccent @Composable get() = MaterialTheme.colorScheme.primaryContainer
val CinemaRoseAccent @Composable get() = MaterialTheme.colorScheme.error

fun translate(key: String, isArabic: Boolean): String {
    val en = mapOf(
        "app_title" to "Novel Weaver AI",
        "shelf_title" to "Your Cinematic Shelf",
        "shelf_subtitle" to "Transform classic literature or paste your own stories into interactive audio-visual movies.",
        "brewing" to "Brewing your shelf classic tales... Please wait.",
        "import_pdf" to "Import PDF Novel",
        "extracting" to "Extracting text... Please wait",
        "select_pdf" to "Select a PDF file to auto-extract text",
        "novel_title" to "Novel Title",
        "author_name" to "Author Name",
        "paste_text" to "Paste Story/Dialogue Chapters",
        "btn_weave" to "WEAVE AI PIPELINE & ANALYZE",
        "stage_read" to "Reading and uploading text content...",
        "stage_chars" to "Identifying and characterizing actors...",
        "stage_scenic" to "Designing visual concept storyboards...",
        "stage_success" to "Story successfully woven! Click Done to open.",
        "done" to "DONE & EXPLORE",
        "actions_header" to "Story Dashboard",
        "play_cinema" to "Live Cinematic Play",
        "char_gallery" to "Character Gallery",
        "storyboard" to "Storyboard Scenes",
        "characters_title" to "Characters Profile Gallery",
        "storyboard_title" to "Storyboard Scenes",
        "delete" to "Delete Novel",
        "scene" to "Scene",
        "narrator" to "NARRATOR",
        "speaking_dialogs" to "Speaking Dialogues",
        "back" to "Back",
        "unknown" to "Unknown"
    )
    val ar = mapOf(
        "app_title" to "نسيج الروايات الذكي",
        "shelf_title" to "رف الروايات السينمائي الخاص بك",
        "shelf_subtitle" to "حوّل رواياتك المفضلة أو المرفوعة ذاتياً إلى مسرحيّات سينمائيّة ناطقة وصور تعبيرية باهرة.",
        "brewing" to "جاري نسج المشاهد والقصص الكلاسيكية... يرجى الانتظار.",
        "import_pdf" to "استيراد رواية PDF",
        "extracting" to "جاري استخراج النص... يرجى الانتظار",
        "select_pdf" to "اختر ملف PDF لاستخراج النص تلقائياً",
        "novel_title" to "عنوان الرواية",
        "author_name" to "اسم الكاتب / المؤلف",
        "paste_text" to "ألصق نص الرواية أو فصول الحوار هنا",
        "btn_weave" to "نسج الرواية وتحليلها بالذكاء الاصطناعي",
        "stage_read" to "جاري قراءة وتحميل محتوى النص...",
        "stage_chars" to "جاري تحديد ورسم ملامح الشخصيات...",
        "stage_scenic" to "جاري تصميم لوحات المشاهد البصرية والمؤثرات...",
        "stage_success" to "تم نسج الرواية بنجاح! اضغط تأكيد للبدء.",
        "done" to "تأكيد واستكشاف الرواية",
        "actions_header" to "لوحة تحكم الرواية",
        "play_cinema" to "عرض العرض السينمائي المباشر",
        "char_gallery" to "معرض وصور الشخصيات",
        "storyboard" to "لوحة مشاهد القصة وتصميمها",
        "characters_title" to "معرض صور وتفاصيل الشخصيات",
        "storyboard_title" to "لوحة القصة والمشاهد المصممة",
        "delete" to "حذف الرواية",
        "scene" to "المشهد",
        "narrator" to "الراوي",
        "speaking_dialogs" to "الحوارات الصوتيّة المتفاعلة",
        "back" to "الرجوع لخلف",
        "unknown" to "غير معروف"
    )
    return if (isArabic) ar[key] ?: en[key] ?: key else en[key] ?: key
}

sealed class Screen {
    object List : Screen()
    object Detail : Screen()
    object Cinema : Screen()
    object Characters : Screen()
    object Storyboard : Screen()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NovelWeaverAppUi(viewModel: NovelViewModel) {
    var showSplash by remember { mutableStateOf(true) }
    var currentScreen by remember { mutableStateOf<Screen>(Screen.List) }
    val selectedNovel by viewModel.currentNovel.collectAsState()
    val isArabic by viewModel.isArabic.collectAsState()
    
    val layoutDirection = if (isArabic) androidx.compose.ui.unit.LayoutDirection.Rtl else androidx.compose.ui.unit.LayoutDirection.Ltr
    
    CompositionLocalProvider(androidx.compose.ui.platform.LocalLayoutDirection provides layoutDirection) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CinemaDarkBackground)
        ) {
            Crossfade(targetState = currentScreen, label = "ScreenTransition") { screen ->
                when (screen) {
                    is Screen.List -> {
                        NovelListScreen(
                            viewModel = viewModel,
                            onNovelSelected = { novelId ->
                                viewModel.selectNovel(novelId)
                                currentScreen = Screen.Detail
                            }
                        )
                    }
                    is Screen.Detail -> {
                        selectedNovel?.let { novel ->
                            NovelDetailScreen(
                                novel = novel,
                                viewModel = viewModel,
                                onBackToEmpty = { currentScreen = Screen.List },
                                onNavigateToCinema = { currentScreen = Screen.Cinema },
                                onNavigateToCharacters = { currentScreen = Screen.Characters },
                                onNavigateToStoryboard = { currentScreen = Screen.Storyboard }
                            )
                        } ?: run {
                            currentScreen = Screen.List
                        }
                    }
                    is Screen.Cinema -> {
                        CinemaPlayerScreen(
                            viewModel = viewModel,
                            onBack = {
                                viewModel.stopVoice()
                                currentScreen = Screen.Detail
                            }
                        )
                    }
                    is Screen.Characters -> {
                        CharacterGalleryScreen(
                            viewModel = viewModel,
                            onBack = { currentScreen = Screen.Detail }
                        )
                    }
                    is Screen.Storyboard -> {
                        StoryboardScreen(
                            viewModel = viewModel,
                            onBack = { currentScreen = Screen.Detail }
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = showSplash,
                enter = fadeIn(),
                exit = fadeOut(animationSpec = tween(600))
            ) {
                CinematicSplashScreen(isArabic = isArabic, onFinished = { showSplash = false })
            }
        }
    }
}

@Composable
fun CinematicSplashScreen(isArabic: Boolean, onFinished: () -> Unit) {
    var startAnim by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (startAnim) 1f else 0.7f,
        animationSpec = tween(1200, easing = LinearOutSlowInEasing)
    )
    val alpha by animateFloatAsState(
        targetValue = if (startAnim) 1f else 0f,
        animationSpec = tween(1000)
    )
    val textAlpha by animateFloatAsState(
        targetValue = if (startAnim) 1f else 0f,
        animationSpec = tween(1000, delayMillis = 600)
    )
    
    LaunchedEffect(key1 = true) {
        startAnim = true
        delay(2600)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D081F)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            
            val goldenPath1 = Path().apply {
                moveTo(-50f, height * 0.35f)
                cubicTo(
                    width * 0.3f, height * 0.15f,
                    width * 0.7f, height * 0.85f,
                    width + 50f, height * 0.45f
                )
            }
            
            val goldenPath2 = Path().apply {
                moveTo(-50f, height * 0.7f)
                cubicTo(
                    width * 0.3f, height * 0.9f,
                    width * 0.7f, height * 0.1f,
                    width + 50f, height * 0.55f
                )
            }
            
            drawPath(
                path = goldenPath1,
                color = Color(0x22D4AF37),
                style = Stroke(width = 4f, cap = StrokeCap.Round)
            )
            
            drawPath(
                path = goldenPath2,
                color = Color(0x186750A4),
                style = Stroke(width = 3f, cap = StrokeCap.Round)
            )
        }
        
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        alpha = alpha
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Color(0x44D4AF37), Color.Transparent)
                            )
                        )
                )
                
                Canvas(modifier = Modifier.size(110.dp)) {
                    val w = size.width
                    val h = size.height
                    
                    val leftWing = Path().apply {
                        moveTo(w * 0.47f, h * 0.36f)
                        cubicTo(w * 0.38f, h * 0.31f, w * 0.22f, h * 0.31f, w * 0.12f, h * 0.35f)
                        lineTo(w * 0.12f, h * 0.70f)
                        cubicTo(w * 0.22f, h * 0.66f, w * 0.38f, h * 0.66f, w * 0.47f, h * 0.71f)
                        close()
                    }
                    
                    val rightWing = Path().apply {
                        moveTo(w * 0.53f, h * 0.36f)
                        cubicTo(w * 0.62f, h * 0.31f, w * 0.78f, h * 0.31f, w * 0.88f, h * 0.35f)
                        lineTo(w * 0.88f, h * 0.70f)
                        cubicTo(w * 0.78f, h * 0.66f, w * 0.62f, h * 0.66f, w * 0.53f, h * 0.71f)
                        close()
                    }
                    
                    val ribbonTop = Path().apply {
                        moveTo(w * 0.10f, h * 0.53f)
                        cubicTo(w * 0.20f, h * 0.25f, w * 0.80f, h * 0.25f, w * 0.90f, h * 0.53f)
                    }
                    val ribbonBottom = Path().apply {
                        moveTo(w * 0.10f, h * 0.53f)
                        cubicTo(w * 0.20f, h * 0.81f, w * 0.80f, h * 0.81f, w * 0.90f, h * 0.53f)
                    }

                    drawPath(ribbonTop, Color(0xFFD4AF37), style = Stroke(width = 5f, cap = StrokeCap.Round))
                    drawPath(ribbonBottom, Color(0xFFD4AF37), style = Stroke(width = 5f, cap = StrokeCap.Round))

                    drawPath(leftWing, Color.White)
                    drawPath(rightWing, Color.White)

                    drawLine(
                        color = Color(0xFFD4AF37),
                        start = Offset(w * 0.5f, h * 0.33f),
                        end = Offset(w * 0.5f, h * 0.73f),
                        strokeWidth = 4f
                    )
                    
                    drawCircle(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFF6750A4), Color(0xFF3F2D75))
                        ),
                        radius = w * 0.13f,
                        center = Offset(w * 0.5f, h * 0.52f)
                    )
                    drawCircle(
                        color = Color(0xFFD4AF37),
                        radius = w * 0.13f,
                        center = Offset(w * 0.5f, h * 0.52f),
                        style = Stroke(width = 2.5f)
                    )
                    
                    val playArrow = Path().apply {
                        moveTo(w * 0.46f, h * 0.46f)
                        lineTo(w * 0.57f, h * 0.52f)
                        lineTo(w * 0.46f, h * 0.58f)
                        close()
                    }
                    drawPath(playArrow, Color.White)
                }
            }
            
            Spacer(modifier = Modifier.height(28.dp))
            
            Text(
                text = if (isArabic) "نسيج الروايات الذكي" else "Novel Weaver AI",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = (-0.5).sp,
                modifier = Modifier.graphicsLayer(alpha = textAlpha)
            )
            
            Spacer(modifier = Modifier.height(6.dp))
            
            Text(
                text = if (isArabic) "جاري نسج الفصول والسينما المباشرة..." else "Weaving classic tales into live cinema...",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xCCD4AF37),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.graphicsLayer(alpha = textAlpha)
            )
        }
    }
}

// -------------------------------------------------------------
// 1. NOVELS LIST SCREEN - Elegant Cinematic Grid
// -------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NovelListScreen(
    viewModel: NovelViewModel,
    onNovelSelected: (Int) -> Unit
) {
    val novelsList by viewModel.novels.collectAsState()
    var showUploadDialog by remember { mutableStateOf(false) }
    val isArabic by viewModel.isArabic.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoStories,
                            contentDescription = "Logo",
                            tint = Color.White,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = translate("app_title", isArabic),
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            letterSpacing = (-0.5).sp
                        )
                    }
                },
                actions = {
                    TextButton(onClick = { viewModel.toggleLanguage() }) {
                        Text(
                            text = if (isArabic) "English" else "العربية",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                    IconButton(onClick = { viewModel.toggleDarkMode() }) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Theme",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CinemaGoldAccent,
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showUploadDialog = true },
                containerColor = CinemaGoldAccent,
                contentColor = Color.White,
                modifier = Modifier
                    .padding(16.dp)
                    .testTag("upload_fab"),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Upload Novel")
            }
        },
        containerColor = CinemaDarkBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Cozy Heading Banner (Bold Typography style!)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(CinemaCardBackground)
                    .border(
                        width = 1.dp,
                        color = Color(0xFFCAC4D0).copy(alpha = 0.4f),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(20.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Column {
                    Text(
                        text = translate("shelf_title", isArabic),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = CinemaGoldAccent,
                        letterSpacing = (-0.5).sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = translate("shelf_subtitle", isArabic),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (novelsList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        CircularProgressIndicator(color = CinemaGoldAccent)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = translate("brewing", isArabic),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(novelsList) { novel ->
                        NovelItemCard(novel = novel, onClick = { onNovelSelected(novel.id) })
                    }
                }
            }
        }
    }

    if (showUploadDialog) {
        NovelUploadDialog(
            viewModel = viewModel,
            onDismiss = { showUploadDialog = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NovelItemCard(novel: Novel, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .testTag("novel_card_${novel.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CinemaSurface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Book Cover Image
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(novel.coverImage ?: "https://images.unsplash.com/photo-1543002588-bfa74002ed7e?w=400")
                    .crossfade(true)
                    .build(),
                contentDescription = novel.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .drawBehind {
                        // Ambient dark overlay Gradient so text stands out
                        val brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f)),
                            startY = 100f
                        )
                        drawRect(brush)
                    }
            )

            // Content Info Overlay
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                // Status Pill
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = when (novel.status) {
                                    "analyzed" -> Color(0xFFEADDFF)
                                    "processing" -> Color(0xFFE8DEF8)
                                    else -> Color(0xFFF3EDF7)
                                },
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = novel.status.uppercase(),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            color = when (novel.status) {
                                "analyzed" -> Color(0xFF21005D)
                                "processing" -> Color(0xFF1D192B)
                                else -> Color(0xFF49454F)
                            },
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                     text = novel.title,
                     maxLines = 2,
                     overflow = TextOverflow.Ellipsis,
                     style = MaterialTheme.typography.titleMedium,
                     fontWeight = FontWeight.Black,
                     color = Color.White,
                     letterSpacing = (-0.3).sp
                )

                Text(
                     text = "by ${novel.author}",
                     style = MaterialTheme.typography.bodySmall,
                     color = Color(0xFFE8DEF8),
                     fontWeight = FontWeight.SemiBold,
                     maxLines = 1,
                     overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// -------------------------------------------------------------
// AI ANALYSIS LOADER / UPLOAD DIALOG
// -------------------------------------------------------------
@Composable
fun NovelUploadDialog(
    viewModel: NovelViewModel,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var textContent by remember { mutableStateOf("") }
    
    val status by viewModel.analysisStatus.collectAsState()

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isExtractingPdf by remember { mutableStateOf(false) }
    var pdfError by remember { mutableStateOf<String?>(null) }

    val pdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                isExtractingPdf = true
                pdfError = null
                try {
                    val fileName = getFileNameFromUri(context, it) ?: "Novel.pdf"
                    val (parsedTitle, parsedAuthor) = parseFileName(fileName)
                    
                    val extractedText = withContext(Dispatchers.IO) {
                        var inputStream: InputStream? = null
                        var reader: PdfReader? = null
                        try {
                            inputStream = context.contentResolver.openInputStream(it)
                            reader = PdfReader(inputStream)
                            val numberOfPages = reader.numberOfPages
                            val stringBuilder = StringBuilder()
                            for (i in 1..numberOfPages) {
                                val pageText = PdfTextExtractor.getTextFromPage(reader, i)
                                stringBuilder.append(pageText).append("\n")
                            }
                            stringBuilder.toString()
                        } finally {
                            reader?.close()
                            inputStream?.close()
                        }
                    }
                    
                    if (extractedText.isBlank()) {
                        pdfError = "Couldn't extract text from this PDF (scanned image PDFs without text layer/OCR are not supported)."
                    } else {
                        textContent = extractedText
                        title = parsedTitle
                        if (parsedAuthor.isNotEmpty()) {
                            author = parsedAuthor
                        }
                    }
                } catch (e: Exception) {
                    pdfError = "Failed to parse PDF: ${e.localizedMessage}"
                } finally {
                    isExtractingPdf = false
                }
            }
        }
    }

    val isArabic by viewModel.isArabic.collectAsState()

    Dialog(onDismissRequest = { if (status == NovelViewModel.AnalysisStatus.Idle) onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CinemaCardBackground)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = translate("import_pdf", isArabic),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = CinemaGoldAccent,
                    letterSpacing = (-0.5).sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                when (status) {
                    is NovelViewModel.AnalysisStatus.Idle -> {
                        // PDF Import Panel
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                                .border(
                                    width = 1.dp,
                                    color = CinemaGoldAccent.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = CinemaTealAccent)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(enabled = !isExtractingPdf) {
                                        pdfLauncher.launch("application/pdf")
                                    }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PictureAsPdf,
                                    contentDescription = "PDF Icon",
                                    tint = CinemaGoldAccent,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = translate("import_pdf", isArabic),
                                        fontWeight = FontWeight.Bold,
                                        color = CinemaGoldAccent,
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    Text(
                                        text = if (isExtractingPdf) translate("extracting", isArabic) else translate("select_pdf", isArabic),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (isExtractingPdf) {
                                    CircularProgressIndicator(
                                        color = CinemaGoldAccent,
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.FileUpload,
                                        contentDescription = "Upload Icon",
                                        tint = CinemaGoldAccent
                                    )
                                }
                            }
                        }

                        if (pdfError != null) {
                            Text(
                                text = pdfError!!,
                                color = CinemaRoseAccent,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text(translate("novel_title", isArabic), color = Color(0xFF49454F), fontWeight = FontWeight.Medium) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("upload_title"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = CinemaGoldAccent,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedBorderColor = CinemaGoldAccent,
                                unfocusedBorderColor = Color(0xFFCAC4D0)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = author,
                            onValueChange = { author = it },
                            label = { Text(translate("author_name", isArabic), color = Color(0xFF49454F), fontWeight = FontWeight.Medium) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = CinemaGoldAccent,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedBorderColor = CinemaGoldAccent,
                                unfocusedBorderColor = Color(0xFFCAC4D0)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = textContent,
                            onValueChange = { textContent = it },
                            label = { Text(translate("paste_text", isArabic), color = Color(0xFF49454F), fontWeight = FontWeight.Medium) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            maxLines = 12,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = CinemaGoldAccent,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedBorderColor = CinemaGoldAccent,
                                unfocusedBorderColor = Color(0xFFCAC4D0)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                if (title.isNotBlank() && textContent.isNotBlank()) {
                                    viewModel.analyzeNovel(
                                        title = title,
                                        author = author.ifBlank { "Unknown" },
                                        textContent = textContent
                                    )
                                }
                            },
                            enabled = title.isNotBlank() && textContent.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CinemaGoldAccent,
                                contentColor = Color.White,
                                disabledContainerColor = CinemaGoldAccent.copy(alpha = 0.5f),
                                disabledContentColor = Color.White.copy(alpha = 0.5f)
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("submit_upload_button")
                        ) {
                            Text(translate("btn_weave", isArabic), fontWeight = FontWeight.Black, letterSpacing = 1.sp, fontSize = 12.sp)
                        }
                    }
                    is NovelViewModel.AnalysisStatus.ProcessingText -> {
                        PipelineSpinner(progressText = translate("stage_read", isArabic))
                    }
                    is NovelViewModel.AnalysisStatus.DiscoveringCharacters -> {
                        PipelineSpinner(progressText = translate("stage_chars", isArabic))
                    }
                    is NovelViewModel.AnalysisStatus.DesigningScenes -> {
                        PipelineSpinner(progressText = translate("stage_scenic", isArabic))
                    }
                    is NovelViewModel.AnalysisStatus.Success -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Success",
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(text = translate("stage_success", isArabic), color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { onDismiss() }, 
                                colors = ButtonDefaults.buttonColors(containerColor = CinemaGoldAccent, contentColor = Color.White),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(translate("done", isArabic), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    is NovelViewModel.AnalysisStatus.Error -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = "Error",
                                tint = CinemaRoseAccent,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = (status as NovelViewModel.AnalysisStatus.Error).errorMessage,
                                color = CinemaRoseAccent,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = onDismiss, 
                                colors = ButtonDefaults.buttonColors(containerColor = CinemaRoseAccent, contentColor = Color.White),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(if (isArabic) "إغلاق" else "CLOSE", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PipelineSpinner(progressText: String) {
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(color = CinemaGoldAccent, modifier = Modifier.size(50.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = progressText,
            color = Color(0xFF1D1B20),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

// -------------------------------------------------------------
// 2. INTERACTIVE DASHBOARD & EVENT TIMELINE
// -------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NovelDetailScreen(
    novel: Novel,
    viewModel: NovelViewModel,
    onBackToEmpty: () -> Unit,
    onNavigateToCinema: () -> Unit,
    onNavigateToCharacters: () -> Unit,
    onNavigateToStoryboard: () -> Unit
) {
    val charList by viewModel.currentCharacters.collectAsState()
    val eventList by viewModel.currentEvents.collectAsState()
    val isArabic by viewModel.isArabic.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = translate("actions_header", isArabic), color = Color.White, fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.5).sp) },
                navigationIcon = {
                    IconButton(onClick = onBackToEmpty) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    TextButton(onClick = { viewModel.toggleLanguage() }) {
                        Text(
                            text = if (isArabic) "English" else "العربية",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                    IconButton(onClick = { viewModel.toggleDarkMode() }) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Theme",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = { viewModel.deleteNovel(novel); onBackToEmpty() }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CinemaGoldAccent)
            )
        },
        containerColor = CinemaDarkBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Creative Hero Banner
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .height(160.dp),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        AsyncImage(
                            model = novel.coverImage ?: "https://images.unsplash.com/photo-1543002588-bfa74002ed7e?w=600",
                            contentDescription = novel.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(0.85f))))
                        )
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.Bottom
                        ) {
                            Text(
                                text = novel.title,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = (-0.5).sp
                            )
                            Text(
                                text = if (isArabic) "بقلم ${novel.author}" else "By ${novel.author}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFEADDFF)
                            )
                        }
                    }
                }
            }

            // Quick Operations Buttons Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DashboardActionButton(
                        icon = Icons.Default.PlayCircle,
                        label = translate("play_cinema", isArabic),
                        color = CinemaGoldAccent,
                        textColor = Color.White,
                        modifier = Modifier.weight(1f).testTag("cinema_player_tab"),
                        onClick = onNavigateToCinema
                    )
                    DashboardActionButton(
                        icon = Icons.Default.People,
                        label = translate("char_gallery", isArabic),
                        color = CinemaCardBackground,
                        textColor = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f).testTag("characters_tab"),
                        onClick = onNavigateToCharacters
                    )
                    DashboardActionButton(
                        icon = Icons.Default.GridOn,
                        label = translate("storyboard", isArabic),
                        color = CinemaCardBackground,
                        textColor = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f).testTag("storyboard_tab"),
                        onClick = onNavigateToStoryboard
                    )
                }
            }

            // Timeline Header
            item {
                Text(
                    text = if (isArabic) "التسلسل الزمني لأحداث الرواية" else "Story Chronology Timeline",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onBackground,
                    letterSpacing = (-0.3).sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Event Cards mapped chronologically
            if (eventList.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = if (isArabic) "لا توجد مشاهد مستخرجة حالياً." else "No events extracted. Analyze the novel first.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)
                    }
                }
            } else {
                items(eventList) { ev ->
                    EventTimelineCard(
                        event = ev,
                        characters = charList,
                        onPlayScene = {
                            viewModel.setCinemaEventIndex(eventList.indexOf(ev))
                            onNavigateToCinema()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DashboardActionButton(
    icon: ImageVector,
    label: String,
    color: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues(8.dp),
        modifier = modifier.height(64.dp)
            .border(
                width = 1.dp,
                color = if (color == CinemaGoldAccent) Color.Transparent else Color(0xFFCAC4D0).copy(alpha = 0.4f),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon, 
                contentDescription = label, 
                tint = if (color == CinemaGoldAccent) Color.White else CinemaGoldAccent,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label.uppercase(), 
                fontSize = 9.sp, 
                fontWeight = FontWeight.Black, 
                color = textColor,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
fun EventTimelineCard(
    event: Event,
    characters: List<Character>,
    onPlayScene: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("event_timeline_card_${event.id}")
            .border(
                width = 1.dp,
                color = Color(0xFFCAC4D0).copy(alpha = 0.4f),
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CinemaSurface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Small Tone-Color Bar
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(90.dp)
                    .clip(CircleShape)
                    .background(
                        when (event.emotionalTone.lowercase()) {
                            "tense" -> Color(0xFFB3261E)
                            "mysterious" -> CinemaGoldAccent
                            "joyful" -> Color(0xFF2E7D32)
                            "romantic" -> Color(0xFFE91E63)
                            "peaceful" -> Color(0xFF00ACC1)
                            else -> CinemaGoldAccent
                        }
                    )
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "SCENE ${event.sequence}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = CinemaGoldAccent,
                        letterSpacing = 0.5.sp
                    )
                    
                    Box(
                        modifier = Modifier
                            .background(CinemaTealAccent, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = event.emotionalTone.uppercase(),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF21005D),
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF1D1B20),
                    letterSpacing = (-0.3).sp
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = CinemaGoldAccent.copy(alpha = 0.7f),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = event.location,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF49454F)
                    )
                }

                Text(
                    text = event.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF49454F),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Action play scene
            IconButton(
                onClick = onPlayScene,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .size(44.dp)
                    .background(CinemaTealAccent, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play Scene",
                    tint = CinemaGoldAccent
                )
            }
        }
    }
}

// -------------------------------------------------------------
// 3. CINEMA PLAYER SCREEN - Dark Immersive Movie Mode
// -------------------------------------------------------------
@Composable
fun CinemaPlayerScreen(
    viewModel: NovelViewModel,
    onBack: () -> Unit
) {
    val events by viewModel.currentEvents.collectAsState()
    val isPlaying by viewModel.cinemaPlaying.collectAsState()
    val eventIndex by viewModel.currentCinemaEventIndex.collectAsState()
    val activeSpeaker by viewModel.activeSpeakerName.collectAsState()
    val activeDialogue by viewModel.activeDialogueText.collectAsState()
    val isArabic by viewModel.isArabic.collectAsState()
 
    val currentEvent = events.getOrNull(eventIndex)
 
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("cinema_player_root")
    ) {
        if (currentEvent == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = CinemaTealAccent)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = if (isArabic) "جاري تحميل شرائح العرض السينمائي..." else "Loading Cinema Slides...", color = Color.White)
                }
            }
        } else {
            // Large Cinematic Background Portrait with subtle scale effect
            val infiniteTransition = rememberInfiniteTransition(label = "ScaleTransition")
            val scaleAnim by infiniteTransition.animateFloat(
                initialValue = 1.0f,
                targetValue = 1.05f,
                animationSpec = infiniteRepeatable(
                    animation = tween(8000, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "ScaleFactor"
            )
 
            AsyncImage(
                model = currentEvent.imageUrl ?: "https://images.unsplash.com/photo-1543002588-bfa74002ed7e?w=800",
                contentDescription = currentEvent.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .scale(scaleAnim)
            )
 
            // Cinematic black vignetting
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.6f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.95f)
                            )
                        )
                    )
            )
 
            // Back button
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .padding(16.dp)
                    .padding(top = 16.dp)
                    .size(44.dp)
                    .background(Color.Black.copy(0.5f), CircleShape)
                    .align(Alignment.TopStart)
            ) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close player", tint = Color.White)
            }
 
            // Dialogue Subtitle Box Overlay (Bottom)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(24.dp)
                    .padding(bottom = 80.dp)
            ) {
                // Audio Wave Visualizer on Voice Acting Active State
                if (isPlaying) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SpeakingVisualizerLines()
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isArabic) "الأداء الصوتي متفاعل ومستمر" else "VOICE ACTING ACTIVE",
                            style = MaterialTheme.typography.labelSmall,
                            color = CinemaTealAccent,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
 
                // Subtitle Display
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.75f), RoundedCornerShape(12.dp))
                        .border(1.dp, Color.DarkGray.copy(0.4f), RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        if (activeSpeaker != null) {
                            Text(
                                text = activeSpeaker!!,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = CinemaTealAccent,
                                letterSpacing = (-0.3).sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = activeDialogue ?: "",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White,
                                lineHeight = 24.sp
                            )
                        } else {
                            Text(
                                text = translate("narrator", isArabic),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Black,
                                color = Color.LightGray,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = currentEvent.description,
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White,
                                lineHeight = 24.sp
                            )
                        }
                    }
                }
            }
 
            // Cinema Navigation / Player Controls
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(Color.Black.copy(0.85f))
                    .padding(vertical = 12.dp, horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isArabic) "المشهد ${currentEvent.sequence}: ${currentEvent.title}" else "Scene ${currentEvent.sequence}: ${currentEvent.title}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            letterSpacing = (-0.3).sp
                        )
                        Text(
                            text = currentEvent.location,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.LightGray
                        )
                    }
 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { viewModel.setCinemaEventIndex(eventIndex - 1) },
                            enabled = eventIndex > 0
                        ) {
                            Icon(imageVector = Icons.Default.SkipPrevious, contentDescription = "Previous", tint = if (eventIndex > 0) Color.White else Color.DarkGray)
                        }
 
                        IconButton(
                            onClick = { viewModel.toggleCinemaPlayback() },
                            modifier = Modifier
                                .size(50.dp)
                                .background(CinemaTealAccent, CircleShape)
                                .testTag("cinema_play_pause_button")
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = CinemaGoldAccent,
                                modifier = Modifier.size(30.dp)
                            )
                        }
 
                        IconButton(
                            onClick = { viewModel.setCinemaEventIndex(eventIndex + 1) },
                            enabled = eventIndex < events.size - 1
                        ) {
                            Icon(imageVector = Icons.Default.SkipNext, contentDescription = "Next", tint = if (eventIndex < events.size - 1) Color.White else Color.DarkGray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SpeakingVisualizerLines() {
    val infiniteTransition = rememberInfiniteTransition(label = "Waves")
    
    val f1 by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(tween(400, delayMillis = 100), RepeatMode.Reverse),
        label = "F1"
    )
    val f2 by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(tween(350, delayMillis = 0), RepeatMode.Reverse),
        label = "F2"
    )
    val f3 by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(tween(450, delayMillis = 200), RepeatMode.Reverse),
        label = "F3"
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height((f1 * 24).dp)
                .background(CinemaTealAccent)
        )
        Box(
            modifier = Modifier
                .width(3.dp)
                .height((f2 * 24).dp)
                .background(CinemaTealAccent)
        )
        Box(
            modifier = Modifier
                .width(3.dp)
                .height((f3 * 24).dp)
                .background(CinemaTealAccent)
        )
    }
}

// -------------------------------------------------------------
// 4. CHARACTER PORTRAITS GALLERY SCREEN
// -------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterGalleryScreen(
    viewModel: NovelViewModel,
    onBack: () -> Unit
) {
    val chars by viewModel.currentCharacters.collectAsState()
    var selectedChar by remember { mutableStateOf<Character?>(null) }

    val isArabic by viewModel.isArabic.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = translate("characters_title", isArabic), color = Color.White, fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.5).sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CinemaGoldAccent)
            )
        },
        containerColor = CinemaDarkBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            if (chars.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = if (isArabic) "لم يتم العثور على أي شخصية." else "No characters found.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)
                }
            } else {
                Spacer(modifier = Modifier.height(16.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(chars) { char ->
                        Card(
                            onClick = { selectedChar = char },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = CinemaSurface),
                            elevation = CardDefaults.cardElevation(2.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(230.dp)
                                .testTag("character_card_${char.id}")
                                .border(
                                    width = 1.dp,
                                    color = Color(0xFFCAC4D0).copy(alpha = 0.4f),
                                    shape = RoundedCornerShape(16.dp)
                                )
                        ) {
                            Column(modifier = Modifier.fillMaxSize()) {
                                AsyncImage(
                                    model = char.imageUrl ?: "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=300",
                                    contentDescription = char.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(140.dp)
                                )

                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = char.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF1D1B20),
                                        letterSpacing = (-0.3).sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                color = when (char.role.lowercase()) {
                                                    "protagonist" -> Color(0xFFEADDFF)
                                                    "antagonist" -> Color(0xFFF9DEDC)
                                                    else -> Color(0xFFF3EDF7)
                                                },
                                                shape = RoundedCornerShape(6.dp)
                                            )
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = char.role.uppercase(),
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Black,
                                            color = when (char.role.lowercase()) {
                                                "protagonist" -> Color(0xFF21005D)
                                                "antagonist" -> Color(0xFF410E0B)
                                                else -> Color(0xFF49454F)
                                            },
                                            letterSpacing = 0.5.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Detail Dialog Sheet on Character Selection
    selectedChar?.let { char ->
        Dialog(onDismissRequest = { selectedChar = null }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .border(
                        width = 1.dp,
                        color = Color(0xFFCAC4D0).copy(alpha = 0.4f),
                        shape = RoundedCornerShape(24.dp)
                    ),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CinemaSurface)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Profile Header Image
                    AsyncImage(
                        model = char.imageUrl ?: "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=300",
                        contentDescription = char.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = char.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF1D1B20),
                        letterSpacing = (-0.5).sp
                    )

                    Row(
                        modifier = Modifier.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = when (char.role.lowercase()) {
                                        "protagonist" -> Color(0xFFEADDFF)
                                        "antagonist" -> Color(0xFFF9DEDC)
                                        else -> Color(0xFFF3EDF7)
                                    },
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = char.role.uppercase(),
                                fontSize = 8.sp,
                                color = when (char.role.lowercase()) {
                                    "protagonist" -> Color(0xFF21005D)
                                    "antagonist" -> Color(0xFF410E0B)
                                    else -> Color(0xFF49454F)
                                },
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Aliases: ${char.aliases.ifBlank { "None" }}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF49454F)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    CharacterInfoSection(title = "Personality Profile", body = char.personality)
                    CharacterInfoSection(title = "Physical Appearance Context", body = char.physicalDescription)
                    CharacterInfoSection(title = "Voice/Speaking Style", body = char.speakingStyle)
                    CharacterInfoSection(title = "Internal Relationships", body = char.relationshipsJson)

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { selectedChar = null },
                        colors = ButtonDefaults.buttonColors(containerColor = CinemaGoldAccent, contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text("BACK TO PROFILES", fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun CharacterInfoSection(title: String, body: String) {
    Column(modifier = Modifier.padding(bottom = 12.dp)) {
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            color = CinemaGoldAccent,
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = body,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1D1B20)
        )
    }
}

// -------------------------------------------------------------
// 5. STORYBOARD GRID SCREEN (Printable Panel Grid)
// -------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryboardScreen(
    viewModel: NovelViewModel,
    onBack: () -> Unit
) {
    val events by viewModel.currentEvents.collectAsState()

    val isArabic by viewModel.isArabic.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = translate("storyboard_title", isArabic), color = Color.White, fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.5).sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CinemaGoldAccent)
            )
        },
        containerColor = CinemaDarkBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            if (events.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = if (isArabic) "لا توجد لوحات مشاهد متوفرة حالياً." else "No storyboard panels available yet.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)
                }
            } else {
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize().testTag("storyboard_list"),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(events) { e ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = CinemaSurface),
                            elevation = CardDefaults.cardElevation(2.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = 1.dp,
                                    color = Color(0xFFCAC4D0).copy(alpha = 0.4f),
                                    shape = RoundedCornerShape(16.dp)
                                )
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                // Storyboard Visual panel
                                AsyncImage(
                                    model = e.imageUrl ?: "https://images.unsplash.com/photo-1543002588-bfa74002ed7e?w=600",
                                    contentDescription = e.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp)
                                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                                )

                                // Storyboard text details below panel
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "PANEL ${e.sequence}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Black,
                                            color = CinemaGoldAccent,
                                            letterSpacing = 0.5.sp
                                        )
                                        Box(
                                            modifier = Modifier
                                                .background(CinemaTealAccent, RoundedCornerShape(6.dp))
                                                .padding(horizontal = 8.dp, vertical = 3.dp)
                                        ) {
                                            Text(
                                                text = "TONE: ${e.emotionalTone.uppercase()}",
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color(0xFF21005D),
                                                letterSpacing = 0.5.sp
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = e.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF1D1B20),
                                        letterSpacing = (-0.3).sp
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = "LOCATION: ${e.location.uppercase()}",
                                        fontSize = 11.sp,
                                        color = CinemaGoldAccent.copy(alpha = 0.8f),
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = e.description,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF49454F)
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(CinemaCardBackground, RoundedCornerShape(12.dp))
                                            .border(
                                                width = 1.dp,
                                                color = Color(0xFFCAC4D0).copy(alpha = 0.2f),
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .padding(12.dp)
                                    ) {
                                        Column {
                                            Text(
                                                text = "AI IMAGE PROMPT",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Black,
                                                color = CinemaGoldAccent,
                                                letterSpacing = 0.5.sp
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = e.visualDescription,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = Color(0xFF1D1B20)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Helper: Extract name from selection Uri
fun getFileNameFromUri(context: android.content.Context, uri: Uri): String? {
    var result: String? = null
    if (uri.scheme == "content") {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (index != -1) {
                    result = it.getString(index)
                }
            }
        }
    }
    if (result == null) {
        result = uri.path
        val cut = result?.lastIndexOf('/')
        if (cut != null && cut != -1) {
            result = result?.substring(cut + 1)
        }
    }
    return result
}

// Helper: Smart parsing of title and author from file name
fun parseFileName(fileName: String): Pair<String, String> {
    val clean = fileName.replace(Regex("\\.pdf$", RegexOption.IGNORE_CASE), "").trim()
    val separators = listOf(" - ", " by ", " By ", " _ ")
    for (sep in separators) {
        if (clean.contains(sep)) {
            val parts = clean.split(sep, limit = 2)
            val t = parts[0].trim().replace("_", " ").replace("-", " ")
            val a = parts[1].trim().replace("_", " ").replace("-", " ")
            if (t.isNotEmpty() && a.isNotEmpty()) {
                return Pair(t, a)
            }
        }
    }
    return Pair(clean.replace("_", " ").replace("-", " "), "")
}

