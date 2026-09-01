package com.example.gallerycompose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application

import gallerycompose.shared.generated.resources.Res
import gallerycompose.shared.generated.resources.aurora
import gallerycompose.shared.generated.resources.city
import gallerycompose.shared.generated.resources.fire_sky
import gallerycompose.shared.generated.resources.huge_tree
import gallerycompose.shared.generated.resources.lily_flowers
import gallerycompose.shared.generated.resources.perfect_library
import gallerycompose.shared.generated.resources.sunset
import gallerycompose.shared.generated.resources.tinkercad

import org.jetbrains.compose.resources.painterResource

import java.awt.Desktop
import java.awt.FileDialog
import java.awt.Frame
import java.io.File


// ============================================================
// GALLERY ITEM
// ============================================================

data class GalleryItem(
    val title: String,
    val date: String,
    val type: String,
    val image: String,
    val externalPath: String? = null,
    val isVideo: Boolean = false
)


// ============================================================
// ALBUM
// ============================================================

data class Album(
    val name: String,
    val photos: List<String>
)


// ============================================================
// DEFAULT GALLERY
// ============================================================

private val defaultGalleryItems = listOf(

    GalleryItem(
        title = "Lily Flowers",
        date = "Aug 27, 2026",
        type = "flowers",
        image = "lily_flowers.jpeg"
    ),

    GalleryItem(
        title = "Huge Tree",
        date = "Aug 27, 2026",
        type = "tree",
        image = "huge_tree.jpg"
    ),

    GalleryItem(
        title = "TinkerCad",
        date = "Aug 27, 2026",
        type = "tinker",
        image = "tinkercad.png"
    ),

    GalleryItem(
        title = "Fire Sky",
        date = "Aug 27, 2026",
        type = "fire",
        image = "fire_sky.jpg"
    ),

    GalleryItem(
        title = "Aurora",
        date = "Aug 27, 2026",
        type = "aurora",
        image = "aurora.png"
    ),

    GalleryItem(
        title = "Sunset",
        date = "Aug 27, 2026",
        type = "sunset",
        image = "sunset.jpeg"
    ),

    GalleryItem(
        title = "City",
        date = "Aug 27, 2026",
        type = "city",
        image = "city.jpg"
    ),

    GalleryItem(
        title = "Perfect Library",
        date = "Aug 27, 2026",
        type = "library",
        image = "perfect_library.jpg"
    )
)


// ============================================================
// DEFAULT ALBUMS
// ============================================================

private val defaultAlbums = listOf(

    Album(
        name = "Wallpapers",
        photos = listOf(
            "Aurora",
            "Fire Sky",
            "Sunset"
        )
    ),

    Album(
        name = "Memories",
        photos = listOf(
            "Lily Flowers",
            "Huge Tree",
            "City"
        )
    ),

    Album(
        name = "Design",
        photos = listOf(
            "TinkerCad",
            "Perfect Library"
        )
    )
)


// ============================================================
// MAIN
// ============================================================

fun main() = application {

    Window(
        onCloseRequest = ::exitApplication,
        title = "GalleryApp",
        state = WindowState(
            size = DpSize(
                1440.dp,
                900.dp
            )
        )
    ) {

        GalleryApp()
    }
}


// ============================================================
// GALLERY APP
// ============================================================

@Composable
fun GalleryApp() {

    // --------------------------------------------------------
    // GALLERY DATA
    // --------------------------------------------------------

    val galleryItems =
        remember {

            mutableStateListOf<GalleryItem>().apply {
                addAll(defaultGalleryItems)
            }
        }


    // --------------------------------------------------------
    // ALBUM DATA
    // --------------------------------------------------------

    var albums by remember {
        mutableStateOf(defaultAlbums)
    }


    // --------------------------------------------------------
    // NAVIGATION
    // --------------------------------------------------------

    var currentPage by remember {
        mutableStateOf("Gallery")
    }

    var selectedItem by remember {
        mutableStateOf<GalleryItem?>(null)
    }

    var selectedAlbum by remember {
        mutableStateOf<String?>(null)
    }


    // --------------------------------------------------------
    // SEARCH
    // --------------------------------------------------------

    var searchText by remember {
        mutableStateOf("")
    }


    // --------------------------------------------------------
    // GALLERY SELECTION
    // --------------------------------------------------------

    var selectionMode by remember {
        mutableStateOf(false)
    }

    var selectedItems by remember {
        mutableStateOf(setOf<GalleryItem>())
    }


    // --------------------------------------------------------
    // ALBUM SELECTION
    // --------------------------------------------------------

    var albumEditMode by remember {
        mutableStateOf(false)
    }

    var selectedAlbumItems by remember {
        mutableStateOf(setOf<GalleryItem>())
    }


    // --------------------------------------------------------
    // DIALOGS
    // --------------------------------------------------------

    var showDeleteDialog by remember {
        mutableStateOf(false)
    }

    var showAlbumDialog by remember {
        mutableStateOf(false)
    }

    var showNewAlbumDialog by remember {
        mutableStateOf(false)
    }

    var showDeleteAlbumDialog by remember {
        mutableStateOf(false)
    }

    var showRemoveFromAlbumDialog by remember {
        mutableStateOf(false)
    }


    // --------------------------------------------------------
    // NEW ALBUM NAME
    // --------------------------------------------------------

    var newAlbumName by remember {
        mutableStateOf("")
    }


    // --------------------------------------------------------
    // SEARCH FILTER
    // --------------------------------------------------------

    val filteredItems =
        galleryItems.filter {

            it.title.contains(
                searchText,
                ignoreCase = true
            )
        }


    // ========================================================
    // ADD MEDIA FROM COMPUTER
    // ========================================================

    fun addMediaFromComputer() {

        val fileDialog =
            FileDialog(
                null as Frame?,
                "Media seç",
                FileDialog.LOAD
            )

        fileDialog.isMultipleMode = true

        fileDialog.setFilenameFilter { _, name ->

            val extension =
                name.substringAfterLast(
                    ".",
                    ""
                ).lowercase()

            extension in listOf(

                "jpg",
                "jpeg",
                "png",
                "gif",
                "bmp",

                "mp4",
                "mov",
                "avi",
                "mkv",
                "webm"
            )
        }

        fileDialog.isVisible = true

        val files =
            fileDialog.files


        files.forEach { file ->

            val extension =
                file.extension.lowercase()

            val isVideo =
                extension in listOf(

                    "mp4",
                    "mov",
                    "avi",
                    "mkv",
                    "webm"
                )


            val alreadyExists =
                galleryItems.any {

                    it.externalPath ==
                            file.absolutePath
                }


            if (!alreadyExists) {

                galleryItems.add(

                    GalleryItem(

                        title =
                            file.nameWithoutExtension,

                        date =
                            "Added from computer",

                        type =
                            if (isVideo)
                                "video"
                            else
                                "photo",

                        image =
                            file.name,

                        externalPath =
                            file.absolutePath,

                        isVideo =
                            isVideo
                    )
                )
            }
        }
    }


    // ========================================================
    // DELETE SELECTED PHOTOS
    // ========================================================

    fun deleteSelectedItems() {

        galleryItems.removeAll {

            selectedItems.contains(it)
        }


        // Gallery'den silinen fotoğraf
        // albümlerde de artık bulunmaz.

        albums =
            albums.map { album ->

                album.copy(

                    photos =
                        album.photos.filter { photoName ->

                            selectedItems.none {

                                it.title ==
                                        photoName
                            }
                        }
                )
            }


        selectedItems =
            emptySet()

        selectionMode =
            false

        showDeleteDialog =
            false
    }


    // ========================================================
    // ADD TO ALBUM
    // ========================================================

    fun addSelectedItemsToAlbum(
        albumName: String
    ) {

        albums =
            albums.map { album ->

                if (
                    album.name ==
                    albumName
                ) {

                    val existingPhotos =
                        album.photos.toMutableList()


                    selectedItems.forEach { item ->

                        if (
                            !existingPhotos.contains(
                                item.title
                            )
                        ) {

                            existingPhotos.add(
                                item.title
                            )
                        }
                    }


                    album.copy(
                        photos =
                            existingPhotos
                    )

                } else {

                    album
                }
            }


        selectedItems =
            emptySet()

        selectionMode =
            false

        showAlbumDialog =
            false
    }


    // ========================================================
    // CREATE NEW ALBUM
    // ========================================================

    fun createAlbum() {

        val name =
            newAlbumName.trim()


        if (
            name.isNotEmpty() &&
            albums.none {
                it.name == name
            }
        ) {

            albums =
                albums + Album(

                    name =
                        name,

                    photos =
                        emptyList()
                )
        }


        newAlbumName =
            ""

        showNewAlbumDialog =
            false
    }


    // ========================================================
    // DELETE ALBUM
    // ========================================================

    fun deleteCurrentAlbum() {

        val albumName =
            selectedAlbum


        if (albumName != null) {

            albums =
                albums.filter {

                    it.name !=
                            albumName
                }


            selectedAlbum =
                null

            albumEditMode =
                false

            selectedAlbumItems =
                emptySet()

            showDeleteAlbumDialog =
                false
        }
    }


    // ========================================================
    // REMOVE PHOTO FROM ALBUM ONLY
    // ========================================================

    fun removeSelectedFromAlbum() {

        val albumName =
            selectedAlbum


        if (albumName != null) {

            albums =
                albums.map { album ->

                    if (
                        album.name ==
                        albumName
                    ) {

                        album.copy(

                            photos =
                                album.photos.filter { photoName ->

                                    selectedAlbumItems.none {

                                        it.title ==
                                                photoName
                                    }
                                }
                        )

                    } else {

                        album
                    }
                }


            selectedAlbumItems =
                emptySet()

            albumEditMode =
                false

            showRemoveFromAlbumDialog =
                false
        }
    }


    // ========================================================
    // THEME
    // ========================================================

    MaterialTheme {

        Row(

            modifier =
                Modifier
                    .fillMaxSize()
                    .background(

                        Brush.linearGradient(

                            listOf(

                                Color(0xFF18201E),
                                Color(0xFF101516),
                                Color(0xFF20191E)
                            )
                        )
                    )
        ) {


            // =================================================
            // SIDEBAR
            // =================================================

            Column(

                modifier =
                    Modifier
                        .width(230.dp)
                        .fillMaxHeight()
                        .padding(28.dp),

                verticalArrangement =
                    Arrangement.spacedBy(18.dp)
            ) {

                Text(

                    text =
                        "● GalleryApp",

                    color =
                        Color.White,

                    fontSize =
                        20.sp
                )


                Spacer(
                    modifier =
                        Modifier.height(35.dp)
                )


                SidebarButton(

                    text =
                        "Gallery",

                    selected =
                        currentPage ==
                                "Gallery"

                ) {

                    currentPage =
                        "Gallery"

                    selectedItem =
                        null

                    selectedAlbum =
                        null

                    selectionMode =
                        false

                    selectedItems =
                        emptySet()
                }


                SidebarButton(

                    text =
                        "Media",

                    selected =
                        currentPage ==
                                "Media"

                ) {

                    currentPage =
                        "Media"

                    selectedItem =
                        null

                    selectedAlbum =
                        null

                    selectionMode =
                        false

                    selectedItems =
                        emptySet()
                }


                SidebarButton(

                    text =
                        "Albums",

                    selected =
                        currentPage ==
                                "Albums"

                ) {

                    currentPage =
                        "Albums"

                    selectedItem =
                        null

                    selectedAlbum =
                        null

                    selectionMode =
                        false

                    selectedItems =
                        emptySet()
                }
            }


            // =================================================
            // MAIN
            // =================================================

            Column(

                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(

                            start = 25.dp,
                            end = 55.dp,
                            top = 55.dp,
                            bottom = 35.dp
                        )
            ) {


                // =================================================
                // HEADER
                // =================================================

                Row(

                    modifier =
                        Modifier.fillMaxWidth(),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {


                    Column(

                        modifier =
                            Modifier.weight(1f)
                    ) {

                        Text(

                            text =
                                when {

                                    selectedItem != null ->
                                        selectedItem!!.title

                                    selectedAlbum != null ->
                                        selectedAlbum!!

                                    else ->
                                        currentPage
                                },

                            color =
                                Color.White,

                            fontSize =
                                34.sp
                        )


                        Text(

                            text =
                                when {

                                    selectedItem != null ->
                                        selectedItem!!.date

                                    selectedAlbum != null ->
                                        "Album photos"

                                    currentPage ==
                                            "Media" ->
                                        "Your photos and videos"

                                    else ->
                                        "Your photos and memories"
                                },

                            color =
                                Color.LightGray,

                            fontSize =
                                14.sp
                        )
                    }


                    // =================================================
                    // GALLERY / MEDIA HEADER
                    // =================================================

                    if (

                        selectedItem ==
                        null &&

                        selectedAlbum ==
                        null &&

                        (
                                currentPage ==
                                        "Gallery" ||

                                        currentPage ==
                                        "Media"
                                )

                    ) {

                        Row(

                            horizontalArrangement =
                                Arrangement.spacedBy(
                                    10.dp
                                ),

                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {


                            // -----------------------------------------
                            // SELECT BUTTON
                            // -----------------------------------------

                            Button(

                                onClick = {

                                    if (
                                        selectionMode
                                    ) {

                                        selectionMode =
                                            false

                                        selectedItems =
                                            emptySet()

                                    } else {

                                        selectionMode =
                                            true
                                    }
                                },

                                colors =
                                    ButtonDefaults.buttonColors(

                                        containerColor =
                                            Color(0xFF6840FF),

                                        contentColor =
                                            Color.White
                                    )
                            ) {

                                Text(

                                    if (
                                        selectionMode
                                    )
                                        "Seçimi Bitir"
                                    else
                                        "Seç"
                                )
                            }


                            // -----------------------------------------
                            // SEARCH
                            // -----------------------------------------

                            OutlinedTextField(

                                value =
                                    searchText,

                                onValueChange = {

                                    searchText =
                                        it
                                },

                                placeholder = {

                                    Text(

                                        text =
                                            "Search...",

                                        color =
                                            Color.LightGray
                                    )
                                },

                                singleLine =
                                    true,

                                modifier =
                                    Modifier.width(
                                        180.dp
                                    ),

                                shape =
                                    RoundedCornerShape(
                                        25.dp
                                    ),

                                colors =
                                    OutlinedTextFieldDefaults.colors(

                                        focusedTextColor =
                                            Color.White,

                                        unfocusedTextColor =
                                            Color.White,

                                        focusedBorderColor =
                                            Color(0xFF8040FF),

                                        unfocusedBorderColor =
                                            Color(0xFF8040FF),

                                        cursorColor =
                                            Color.White
                                    )
                            )
                        }
                    }


                    // =================================================
                    // NEW ALBUM
                    // =================================================

                    if (

                        currentPage ==
                        "Albums" &&

                        selectedAlbum ==
                        null

                    ) {

                        Button(

                            onClick = {

                                showNewAlbumDialog =
                                    true
                            },

                            colors =
                                ButtonDefaults.buttonColors(

                                    containerColor =
                                        Color(0xFF2E9D59),

                                    contentColor =
                                        Color.White
                                )
                        ) {

                            Text(
                                "+ Yeni Albüm"
                            )
                        }
                    }
                }


                Spacer(
                    modifier =
                        Modifier.height(30.dp)
                )


                // =================================================
                // SELECTION ACTIONS
                // =================================================

                if (

                    selectionMode &&

                    selectedItems.isNotEmpty() &&

                    selectedItem == null &&

                    selectedAlbum == null

                ) {

                    Row(

                        horizontalArrangement =
                            Arrangement.spacedBy(
                                12.dp
                            )
                    ) {


                        // -----------------------------------------
                        // DELETE
                        // -----------------------------------------

                        Button(

                            onClick = {

                                showDeleteDialog =
                                    true
                            },

                            colors =
                                ButtonDefaults.buttonColors(

                                    containerColor =
                                        Color(0xFFD32F2F),

                                    contentColor =
                                        Color.White
                                )
                        ) {

                            Text(
                                "Sil"
                            )
                        }


                        // -----------------------------------------
                        // ADD TO ALBUM
                        // -----------------------------------------

                        Button(

                            onClick = {

                                showAlbumDialog =
                                    true
                            },

                            colors =
                                ButtonDefaults.buttonColors(

                                    containerColor =
                                        Color(0xFF2E9D59),

                                    contentColor =
                                        Color.White
                                )
                        ) {

                            Text(
                                "Albüme Ekle"
                            )
                        }
                    }


                    Spacer(
                        modifier =
                            Modifier.height(15.dp)
                    )


                    Text(

                        text =
                            "${selectedItems.size} fotoğraf seçildi",

                        color =
                            Color.LightGray,

                        fontSize =
                            14.sp
                    )


                    Spacer(
                        modifier =
                            Modifier.height(15.dp)
                    )
                }


                // =================================================
                // PHOTO DETAIL
                // =================================================

                if (
                    selectedItem != null
                ) {

                    PhotoDetail(

                        item =
                            selectedItem!!,

                        onBack = {

                            selectedItem =
                                null
                        }
                    )

                } else {


                    // =================================================
                    // PAGES
                    // =================================================

                    when (currentPage) {


                        // =================================================
                        // GALLERY
                        // =================================================

                        "Gallery" -> {

                            Text(

                                text =
                                    "Recent",

                                color =
                                    Color.White,

                                fontSize =
                                    18.sp
                            )


                            Spacer(
                                modifier =
                                    Modifier.height(20.dp)
                            )


                            LazyVerticalGrid(

                                columns =
                                    GridCells.Fixed(
                                        3
                                    ),

                                horizontalArrangement =
                                    Arrangement.spacedBy(
                                        25.dp
                                    ),

                                verticalArrangement =
                                    Arrangement.spacedBy(
                                        25.dp
                                    ),

                                modifier =
                                    Modifier.fillMaxSize()
                            ) {

                                items(

                                    filteredItems,

                                    key = {

                                        it.externalPath
                                            ?: it.image
                                    }

                                ) { item ->

                                    PhotoCard(

                                        item =
                                            item,

                                        selectionMode =
                                            selectionMode,

                                        selected =
                                            selectedItems.contains(
                                                item
                                            ),

                                        onClick = {

                                            if (
                                                selectionMode
                                            ) {

                                                selectedItems =

                                                    if (
                                                        selectedItems.contains(
                                                            item
                                                        )
                                                    ) {

                                                        selectedItems -
                                                                item

                                                    } else {

                                                        selectedItems +
                                                                item
                                                    }

                                            } else {

                                                selectedItem =
                                                    item
                                            }
                                        }
                                    )
                                }
                            }
                        }


                        // =================================================
                        // MEDIA
                        // =================================================

                        "Media" -> {

                            Row(

                                verticalAlignment =
                                    Alignment.CenterVertically,

                                horizontalArrangement =
                                    Arrangement.spacedBy(
                                        15.dp
                                    )
                            ) {

                                Text(

                                    text =
                                        "Your photos and videos",

                                    color =
                                        Color.White,

                                    fontSize =
                                        22.sp
                                )


                                Button(

                                    onClick = {

                                        addMediaFromComputer()
                                    },

                                    colors =
                                        ButtonDefaults.buttonColors(

                                            containerColor =
                                                Color(0xFF2E9D59),

                                            contentColor =
                                                Color.White
                                        )
                                ) {

                                    Text(
                                        "+ Media Ekle"
                                    )
                                }
                            }


                            Spacer(
                                modifier =
                                    Modifier.height(20.dp)
                            )


                            LazyVerticalGrid(

                                columns =
                                    GridCells.Fixed(
                                        3
                                    ),

                                horizontalArrangement =
                                    Arrangement.spacedBy(
                                        25.dp
                                    ),

                                verticalArrangement =
                                    Arrangement.spacedBy(
                                        25.dp
                                    ),

                                modifier =
                                    Modifier.fillMaxSize()
                            ) {

                                items(

                                    filteredItems,

                                    key = {

                                        "media_" +
                                                (
                                                        it.externalPath
                                                            ?: it.image
                                                        )
                                    }

                                ) { item ->

                                    PhotoCard(

                                        item =
                                            item,

                                        selectionMode =
                                            selectionMode,

                                        selected =
                                            selectedItems.contains(
                                                item
                                            ),

                                        onClick = {

                                            if (
                                                selectionMode
                                            ) {

                                                selectedItems =

                                                    if (
                                                        selectedItems.contains(
                                                            item
                                                        )
                                                    ) {

                                                        selectedItems -
                                                                item

                                                    } else {

                                                        selectedItems +
                                                                item
                                                    }

                                            } else {

                                                selectedItem =
                                                    item
                                            }
                                        }
                                    )
                                }
                            }
                        }


                        // =================================================
                        // ALBUMS
                        // =================================================

                        "Albums" -> {

                            if (
                                selectedAlbum ==
                                null
                            ) {


                                // -----------------------------------------
                                // ALBUM LIST
                                // -----------------------------------------

                                LazyVerticalGrid(

                                    columns =
                                        GridCells.Fixed(
                                            2
                                        ),

                                    horizontalArrangement =
                                        Arrangement.spacedBy(
                                            25.dp
                                        ),

                                    verticalArrangement =
                                        Arrangement.spacedBy(
                                            25.dp
                                        ),

                                    modifier =
                                        Modifier.fillMaxSize()
                                ) {

                                    items(

                                        albums,

                                        key = {
                                            it.name
                                        }

                                    ) { album ->

                                        AlbumCard(

                                            album =
                                                album,

                                            onClick = {

                                                selectedAlbum =
                                                    album.name

                                                albumEditMode =
                                                    false

                                                selectedAlbumItems =
                                                    emptySet()
                                            }
                                        )
                                    }
                                }

                            } else {


                                // -----------------------------------------
                                // ALBUM DETAIL
                                // -----------------------------------------

                                val currentAlbum =
                                    albums.firstOrNull {

                                        it.name ==
                                                selectedAlbum
                                    }


                                if (
                                    currentAlbum !=
                                    null
                                ) {

                                    val albumPhotos =
                                        galleryItems.filter {

                                            it.title in
                                                    currentAlbum.photos
                                        }


                                    Row(

                                        horizontalArrangement =
                                            Arrangement.spacedBy(
                                                12.dp
                                            )
                                    ) {


                                        // ---------------------------------
                                        // BACK
                                        // ---------------------------------

                                        Button(

                                            onClick = {

                                                selectedAlbum =
                                                    null

                                                albumEditMode =
                                                    false

                                                selectedAlbumItems =
                                                    emptySet()
                                            }
                                        ) {

                                            Text(
                                                "← Albümlere Dön"
                                            )
                                        }


                                        // ---------------------------------
                                        // DELETE ALBUM
                                        // ---------------------------------

                                        Button(

                                            onClick = {

                                                showDeleteAlbumDialog =
                                                    true
                                            },

                                            colors =
                                                ButtonDefaults.buttonColors(

                                                    containerColor =
                                                        Color(0xFFD32F2F),

                                                    contentColor =
                                                        Color.White
                                                )
                                        ) {

                                            Text(
                                                "Albümü Sil"
                                            )
                                        }


                                        // ---------------------------------
                                        // EDIT ALBUM
                                        // ---------------------------------

                                        Button(

                                            onClick = {

                                                albumEditMode =
                                                    !albumEditMode

                                                selectedAlbumItems =
                                                    emptySet()
                                            },

                                            colors =
                                                ButtonDefaults.buttonColors(

                                                    containerColor =
                                                        Color(0xFF6840FF),

                                                    contentColor =
                                                        Color.White
                                                )
                                        ) {

                                            Text(

                                                if (
                                                    albumEditMode
                                                )
                                                    "Düzenlemeyi Bitir"
                                                else
                                                    "Albümü Düzenle"
                                            )
                                        }


                                        // ---------------------------------
                                        // REMOVE FROM ALBUM
                                        // ---------------------------------

                                        if (
                                            albumEditMode
                                        ) {

                                            Button(

                                                onClick = {

                                                    if (
                                                        selectedAlbumItems.isNotEmpty()
                                                    ) {

                                                        showRemoveFromAlbumDialog =
                                                            true
                                                    }
                                                },

                                                colors =
                                                    ButtonDefaults.buttonColors(

                                                        containerColor =
                                                            Color(0xFFD32F2F),

                                                        contentColor =
                                                            Color.White
                                                    )
                                            ) {

                                                Text(
                                                    "Seçilenleri Albümden Sil"
                                                )
                                            }
                                        }
                                    }


                                    Spacer(
                                        modifier =
                                            Modifier.height(
                                                20.dp
                                            )
                                    )


                                    if (
                                        albumEditMode
                                    ) {

                                        Text(

                                            text =
                                                "Albümden kaldırmak istediğin fotoğrafları seç.",

                                            color =
                                                Color.LightGray,

                                            fontSize =
                                                14.sp
                                        )


                                        Spacer(
                                            modifier =
                                                Modifier.height(
                                                    15.dp
                                                )
                                        )
                                    }


                                    LazyVerticalGrid(

                                        columns =
                                            GridCells.Fixed(
                                                3
                                            ),

                                        horizontalArrangement =
                                            Arrangement.spacedBy(
                                                25.dp
                                            ),

                                        verticalArrangement =
                                            Arrangement.spacedBy(
                                                25.dp
                                            ),

                                        modifier =
                                            Modifier.fillMaxSize()
                                    ) {

                                        items(

                                            albumPhotos,

                                            key = {

                                                "album_" +
                                                        (
                                                                it.externalPath
                                                                    ?: it.image
                                                                )
                                            }

                                        ) { item ->

                                            PhotoCard(

                                                item =
                                                    item,

                                                selectionMode =
                                                    albumEditMode,

                                                selected =
                                                    selectedAlbumItems.contains(
                                                        item
                                                    ),

                                                onClick = {

                                                    if (
                                                        albumEditMode
                                                    ) {

                                                        selectedAlbumItems =

                                                            if (
                                                                selectedAlbumItems.contains(
                                                                    item
                                                                )
                                                            ) {

                                                                selectedAlbumItems -
                                                                        item

                                                            } else {

                                                                selectedAlbumItems +
                                                                        item
                                                            }

                                                    } else {

                                                        selectedItem =
                                                            item
                                                    }
                                                }
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


    // ========================================================
    // DELETE PHOTO DIALOG
    // ========================================================

    if (
        showDeleteDialog
    ) {

        AlertDialog(

            onDismissRequest = {

                showDeleteDialog =
                    false
            },

            title = {

                Text(
                    "Fotoğrafları Sil"
                )
            },

            text = {

                Text(

                    "Seçilen ${selectedItems.size} fotoğrafı galeriden silmek istediğine emin misin?"
                )
            },

            confirmButton = {

                Button(

                    onClick = {

                        deleteSelectedItems()
                    },

                    colors =
                        ButtonDefaults.buttonColors(

                            containerColor =
                                Color(0xFFD32F2F),

                            contentColor =
                                Color.White
                        )
                ) {

                    Text(
                        "Evet, Sil"
                    )
                }
            },

            dismissButton = {

                TextButton(

                    onClick = {

                        showDeleteDialog =
                            false
                    }
                ) {

                    Text(
                        "Vazgeç"
                    )
                }
            }
        )
    }


    // ========================================================
    // ADD TO ALBUM DIALOG
    // ========================================================

    if (
        showAlbumDialog
    ) {

        AlertDialog(

            onDismissRequest = {

                showAlbumDialog =
                    false
            },

            title = {

                Text(
                    "Albüme Ekle"
                )
            },

            text = {

                Column {

                    Text(

                        text =
                            "Fotoğrafları hangi albüme eklemek istiyorsun?",

                        color =
                            Color.DarkGray
                    )


                    Spacer(
                        modifier =
                            Modifier.height(
                                15.dp
                            )
                    )


                    albums.forEach { album ->

                        Button(

                            onClick = {

                                addSelectedItemsToAlbum(
                                    album.name
                                )
                            },

                            modifier =
                                Modifier.fillMaxWidth(),

                            colors =
                                ButtonDefaults.buttonColors(

                                    containerColor =
                                        Color(0xFF2E9D59),

                                    contentColor =
                                        Color.White
                                )
                        ) {

                            Text(
                                "📁 ${album.name}"
                            )
                        }


                        Spacer(
                            modifier =
                                Modifier.height(
                                    8.dp
                                )
                        )
                    }
                }
            },

            confirmButton = {},

            dismissButton = {

                TextButton(

                    onClick = {

                        showAlbumDialog =
                            false
                    }
                ) {

                    Text(
                        "Vazgeç"
                    )
                }
            }
        )
    }


    // ========================================================
    // NEW ALBUM DIALOG
    // ========================================================

    if (
        showNewAlbumDialog
    ) {

        AlertDialog(

            onDismissRequest = {

                showNewAlbumDialog =
                    false
            },

            title = {

                Text(
                    "Yeni Albüm"
                )
            },

            text = {

                OutlinedTextField(

                    value =
                        newAlbumName,

                    onValueChange = {

                        newAlbumName =
                            it
                    },

                    label = {

                        Text(
                            "Albüm adı"
                        )
                    },

                    singleLine =
                        true
                )
            },

            confirmButton = {

                Button(

                    onClick = {

                        createAlbum()
                    },

                    colors =
                        ButtonDefaults.buttonColors(

                            containerColor =
                                Color(0xFF2E9D59),

                            contentColor =
                                Color.White
                        )
                ) {

                    Text(
                        "Oluştur"
                    )
                }
            },

            dismissButton = {

                TextButton(

                    onClick = {

                        showNewAlbumDialog =
                            false

                        newAlbumName =
                            ""
                    }
                ) {

                    Text(
                        "Vazgeç"
                    )
                }
            }
        )
    }


    // ========================================================
    // DELETE ALBUM DIALOG
    // ========================================================

    if (
        showDeleteAlbumDialog
    ) {

        AlertDialog(

            onDismissRequest = {

                showDeleteAlbumDialog =
                    false
            },

            title = {

                Text(
                    "Albümü Sil"
                )
            },

            text = {

                Text(

                    "Bu albümü silmek istediğine emin misin? Fotoğraflar galeriden silinmeyecek."
                )
            },

            confirmButton = {

                Button(

                    onClick = {

                        deleteCurrentAlbum()
                    },

                    colors =
                        ButtonDefaults.buttonColors(

                            containerColor =
                                Color(0xFFD32F2F),

                            contentColor =
                                Color.White
                        )
                ) {

                    Text(
                        "Evet, Albümü Sil"
                    )
                }
            },

            dismissButton = {

                TextButton(

                    onClick = {

                        showDeleteAlbumDialog =
                            false
                    }
                ) {

                    Text(
                        "Vazgeç"
                    )
                }
            }
        )
    }


    // ========================================================
    // REMOVE FROM ALBUM DIALOG
    // ========================================================

    if (
        showRemoveFromAlbumDialog
    ) {

        AlertDialog(

            onDismissRequest = {

                showRemoveFromAlbumDialog =
                    false
            },

            title = {

                Text(
                    "Albümden Kaldır"
                )
            },

            text = {

                Text(

                    "Seçilen fotoğraflar sadece bu albümden kaldırılacak. Galeriden silinmeyecek. Emin misin?"
                )
            },

            confirmButton = {

                Button(

                    onClick = {

                        removeSelectedFromAlbum()
                    },

                    colors =
                        ButtonDefaults.buttonColors(

                            containerColor =
                                Color(0xFFD32F2F),

                            contentColor =
                                Color.White
                        )
                ) {

                    Text(
                        "Evet, Kaldır"
                    )
                }
            },

            dismissButton = {

                TextButton(

                    onClick = {

                        showRemoveFromAlbumDialog =
                            false
                    }
                ) {

                    Text(
                        "Vazgeç"
                    )
                }
            }
        )
    }
}


// ============================================================
// SIDEBAR BUTTON
// ============================================================

@Composable
fun SidebarButton(

    text: String,

    selected: Boolean,

    onClick: () -> Unit

) {

    Box(

        modifier =
            Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(
                    RoundedCornerShape(
                        12.dp
                    )
                )
                .background(

                    if (selected)

                        Color(0xFF6840FF)

                    else

                        Color.Transparent
                )
                .clickable {

                    onClick()
                }
                .padding(
                    horizontal = 18.dp
                ),

        contentAlignment =
            Alignment.CenterStart
    ) {

        Text(

            text =
                "○  $text",

            color =
                Color.White,

            fontSize =
                15.sp
        )
    }
}


// ============================================================
// ALBUM CARD
// ============================================================

@Composable
fun AlbumCard(

    album: Album,

    onClick: () -> Unit

) {

    Column(

        modifier =
            Modifier
                .fillMaxWidth()
                .clip(
                    RoundedCornerShape(
                        16.dp
                    )
                )
                .background(
                    Color(0xFF252C2B)
                )
                .clickable {

                    onClick()
                }
                .padding(
                    20.dp
                )
    ) {

        Text(

            text =
                "📁  ${album.name}",

            color =
                Color.White,

            fontSize =
                20.sp
        )


        Spacer(
            modifier =
                Modifier.height(
                    10.dp
                )
        )


        Text(

            text =
                "${album.photos.size} photos",

            color =
                Color.LightGray,

            fontSize =
                14.sp
        )


        Spacer(
            modifier =
                Modifier.height(
                    12.dp
                )
        )


        if (
            album.photos.isNotEmpty()
        ) {

            Text(

                text =
                    album.photos.joinToString(
                        "  •  "
                    ),

                color =
                    Color.Gray,

                fontSize =
                    13.sp
            )

        } else {

            Text(

                text =
                    "Empty album",

                color =
                    Color.Gray,

                fontSize =
                    13.sp
            )
        }
    }
}


// ============================================================
// PHOTO CARD
// ============================================================

@Composable
fun PhotoCard(

    item: GalleryItem,

    selectionMode: Boolean,

    selected: Boolean,

    onClick: () -> Unit

) {

    Column(

        modifier =
            Modifier
                .width(
                    260.dp
                )
                .clickable {

                    onClick()
                }
    ) {


        Box(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(
                        155.dp
                    )
                    .clip(
                        RoundedCornerShape(
                            12.dp
                        )
                    )
        ) {


            // =================================================
            // VIDEO
            // =================================================

            if (
                item.isVideo
            ) {

                Box(

                    modifier =
                        Modifier
                            .fillMaxSize()
                            .background(
                                Color(0xFF202020)
                            ),

                    contentAlignment =
                        Alignment.Center
                ) {

                    Column(

                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        Text(

                            text =
                                "▶",

                            color =
                                Color.White,

                            fontSize =
                                40.sp
                        )


                        Text(

                            text =
                                "VIDEO",

                            color =
                                Color.White,

                            fontSize =
                                16.sp
                        )
                    }
                }


                // =================================================
                // EXTERNAL IMAGE
                // =================================================

            } else if (
                item.externalPath != null
            ) {

                val bitmap =
                    remember(
                        item.externalPath
                    ) {

                        try {

                            File(
                                item.externalPath!!
                            )
                                .inputStream()
                                .use {

                                    loadImageBitmap(
                                        it
                                    )
                                }

                        } catch (
                            e: Exception
                        ) {

                            null
                        }
                    }


                if (
                    bitmap != null
                ) {

                    Image(

                        painter =
                            BitmapPainter(
                                bitmap
                            ),

                        contentDescription =
                            item.title,

                        modifier =
                            Modifier.fillMaxSize(),

                        contentScale =
                            ContentScale.Crop
                    )

                } else {

                    Box(

                        modifier =
                            Modifier
                                .fillMaxSize()
                                .background(
                                    Color.DarkGray
                                ),

                        contentAlignment =
                            Alignment.Center
                    ) {

                        Text(

                            text =
                                "Görsel yüklenemedi",

                            color =
                                Color.White
                        )
                    }
                }


                // =================================================
                // PROJECT RESOURCE
                // =================================================

            } else {

                Image(

                    painter =
                        painterResource(

                            when (
                                item.image
                            ) {

                                "lily_flowers.jpeg" ->
                                    Res.drawable.lily_flowers

                                "huge_tree.jpg" ->
                                    Res.drawable.huge_tree

                                "tinkercad.png" ->
                                    Res.drawable.tinkercad

                                "fire_sky.jpg" ->
                                    Res.drawable.fire_sky

                                "aurora.png" ->
                                    Res.drawable.aurora

                                "sunset.jpeg" ->
                                    Res.drawable.sunset

                                "city.jpg" ->
                                    Res.drawable.city

                                "perfect_library.jpg" ->
                                    Res.drawable.perfect_library

                                else ->
                                    Res.drawable.lily_flowers
                            }
                        ),

                    contentDescription =
                        item.title,

                    modifier =
                        Modifier.fillMaxSize(),

                    contentScale =
                        ContentScale.Crop
                )
            }


            // =================================================
            // SELECTION CHECK
            // =================================================

            if (
                selectionMode &&
                selected
            ) {

                Box(

                    modifier =
                        Modifier
                            .align(
                                Alignment.TopEnd
                            )
                            .padding(
                                10.dp
                            )
                            .size(
                                32.dp
                            )
                            .clip(
                                RoundedCornerShape(
                                    50
                                )
                            )
                            .background(
                                Color(0xFF6840FF)
                            ),

                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(

                        text =
                            "✓",

                        color =
                            Color.White,

                        fontSize =
                            18.sp
                    )
                }
            }
        }


        Spacer(
            modifier =
                Modifier.height(
                    8.dp
                )
        )


        Text(

            text =
                item.title,

            color =
                Color.White,

            fontSize =
                14.sp
        )


        Text(

            text =
                item.date,

            color =
                Color.Gray,

            fontSize =
                12.sp
        )
    }
}


// ============================================================
// PHOTO DETAIL
// ============================================================

@Composable
fun PhotoDetail(

    item: GalleryItem,

    onBack: () -> Unit

) {

    Column(

        modifier =
            Modifier.fillMaxSize()
    ) {


        Button(

            onClick =
                onBack
        ) {

            Text(
                "← Back"
            )
        }


        Spacer(
            modifier =
                Modifier.height(
                    25.dp
                )
        )


        Box(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(
                        RoundedCornerShape(
                            15.dp
                        )
                    )
        ) {


            // =================================================
            // VIDEO DETAIL
            // =================================================

            if (
                item.isVideo
            ) {

                Box(

                    modifier =
                        Modifier
                            .fillMaxSize()
                            .background(
                                Color(0xFF202020)
                            ),

                    contentAlignment =
                        Alignment.Center
                ) {

                    Column(

                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        Text(

                            text =
                                "▶",

                            color =
                                Color.White,

                            fontSize =
                                70.sp
                        )


                        Spacer(
                            modifier =
                                Modifier.height(
                                    15.dp
                                )
                        )


                        Text(

                            text =
                                item.title,

                            color =
                                Color.White,

                            fontSize =
                                24.sp
                        )


                        Spacer(
                            modifier =
                                Modifier.height(
                                    10.dp
                                )
                        )


                        Text(

                            text =
                                "Video",

                            color =
                                Color.LightGray,

                            fontSize =
                                16.sp
                        )


                        Spacer(
                            modifier =
                                Modifier.height(
                                    25.dp
                                )
                        )


                        Button(

                            onClick = {

                                try {

                                    if (
                                        item.externalPath !=
                                        null
                                    ) {

                                        val file =
                                            File(
                                                item.externalPath
                                            )


                                        if (
                                            file.exists()
                                        ) {

                                            Desktop
                                                .getDesktop()
                                                .open(
                                                    file
                                                )
                                        }
                                    }

                                } catch (
                                    e: Exception
                                ) {

                                    e.printStackTrace()
                                }
                            },

                            colors =
                                ButtonDefaults.buttonColors(

                                    containerColor =
                                        Color(0xFF2E9D59),

                                    contentColor =
                                        Color.White
                                )
                        ) {

                            Text(
                                "▶ Videoyu Oynat"
                            )
                        }
                    }
                }


                // =================================================
                // IMAGE DETAIL
                // =================================================

            } else {

                if (
                    item.externalPath !=
                    null
                ) {

                    val bitmap =
                        remember(
                            item.externalPath
                        ) {

                            try {

                                File(
                                    item.externalPath!!
                                )
                                    .inputStream()
                                    .use {

                                        loadImageBitmap(
                                            it
                                        )
                                    }

                            } catch (
                                e: Exception
                            ) {

                                null
                            }
                        }


                    if (
                        bitmap != null
                    ) {

                        Image(

                            painter =
                                BitmapPainter(
                                    bitmap
                                ),

                            contentDescription =
                                item.title,

                            modifier =
                                Modifier.fillMaxSize(),

                            contentScale =
                                ContentScale.Fit
                        )

                    } else {

                        Box(

                            modifier =
                                Modifier.fillMaxSize(),

                            contentAlignment =
                                Alignment.Center
                        ) {

                            Text(

                                text =
                                    "Görsel yüklenemedi",

                                color =
                                    Color.White
                            )
                        }
                    }

                } else {

                    Image(

                        painter =
                            painterResource(

                                when (
                                    item.image
                                ) {

                                    "lily_flowers.jpeg" ->
                                        Res.drawable.lily_flowers

                                    "huge_tree.jpg" ->
                                        Res.drawable.huge_tree

                                    "tinkercad.png" ->
                                        Res.drawable.tinkercad

                                    "fire_sky.jpg" ->
                                        Res.drawable.fire_sky

                                    "aurora.png" ->
                                        Res.drawable.aurora

                                    "sunset.jpeg" ->
                                        Res.drawable.sunset

                                    "city.jpg" ->
                                        Res.drawable.city

                                    "perfect_library.jpg" ->
                                        Res.drawable.perfect_library

                                    else ->
                                        Res.drawable.lily_flowers
                                }
                            ),

                        contentDescription =
                            item.title,

                        modifier =
                            Modifier.fillMaxSize(),

                        contentScale =
                            ContentScale.Fit
                    )
                }
            }
        }
    }
}


// ============================================================
// OLD GRADIENT FUNCTION
// ============================================================

fun photoGradient(
    type: String
): Brush {

    return when (type) {

        "flowers" ->

            Brush.linearGradient(

                listOf(
                    Color(0xFF55483D),
                    Color(0xFFD7C8A8)
                )
            )


        "tree" ->

            Brush.linearGradient(

                listOf(
                    Color(0xFF0B4D3A),
                    Color(0xFF8B4E2D)
                )
            )


        "tinker" ->

            Brush.linearGradient(

                listOf(
                    Color(0xFFE4E1D8),
                    Color(0xFF9DA4B3)
                )
            )


        "fire" ->

            Brush.linearGradient(

                listOf(
                    Color(0xFFFF3D00),
                    Color(0xFFFFA000)
                )
            )


        "aurora" ->

            Brush.linearGradient(

                listOf(
                    Color(0xFF081D38),
                    Color(0xFF00D97E),
                    Color(0xFF14245B)
                )
            )


        "sunset" ->

            Brush.linearGradient(

                listOf(
                    Color(0xFF5D1F19),
                    Color(0xFFFF9F32)
                )
            )


        "city" ->

            Brush.linearGradient(

                listOf(
                    Color(0xFF111111),
                    Color(0xFF777777)
                )
            )


        "library" ->

            Brush.linearGradient(

                listOf(
                    Color(0xFF152A3B),
                    Color(0xFF9D743A)
                )
            )


        else ->

            Brush.linearGradient(

                listOf(
                    Color.DarkGray,
                    Color.Gray
                )
            )
    }
}