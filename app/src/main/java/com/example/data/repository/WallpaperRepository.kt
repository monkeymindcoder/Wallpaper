package com.example.data.repository

import com.example.data.local.WallpaperDao
import com.example.data.model.Wallpaper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class WallpaperRepository(private val wallpaperDao: WallpaperDao) {

    val allWallpapers: Flow<List<Wallpaper>> = wallpaperDao.getAllWallpapers()
    val favoriteWallpapers: Flow<List<Wallpaper>> = wallpaperDao.getFavoriteWallpapers()

    fun getWallpapersByCategory(category: String): Flow<List<Wallpaper>> {
        return wallpaperDao.getWallpapersByCategory(category)
    }

    suspend fun getWallpaperById(id: String): Wallpaper? {
        return wallpaperDao.getWallpaperById(id)
    }

    suspend fun toggleFavorite(id: String, currentStatus: Boolean) {
        wallpaperDao.updateFavoriteStatus(id, !currentStatus)
    }

    suspend fun incrementDownloadCount(id: String) {
        wallpaperDao.incrementDownloadCount(id)
    }

    suspend fun insertWallpapers(wallpapers: List<Wallpaper>) {
        wallpaperDao.insertWallpapers(wallpapers)
    }

    suspend fun checkAndSeedDatabase() {
        val currentList = wallpaperDao.getAllWallpapers().first()
        if (currentList.isEmpty()) {
            wallpaperDao.insertWallpapers(getSeedWallpapers())
        } else if (currentList.size < getSeedWallpapers().size) {
            val existingIds = currentList.map { it.id }.toSet()
            val newWallpapers = getSeedWallpapers().filter { it.id !in existingIds }
            wallpaperDao.insertWallpapers(newWallpapers)
        }
    }

    private fun getSeedWallpapers(): List<Wallpaper> {
        val seedRaw = listOf(
            // Nature (1-20)
            RawWallpaper("https://images.unsplash.com/photo-1501854140801-50d01698950b", "Emerald Valleys", "Nature", "Kamil Kalbarczyk", 120),
            RawWallpaper("https://images.unsplash.com/photo-1470071459604-3b5ec3a7fe05", "Golden Forest Mist", "Nature", "Lukasz Szmigiel", 340),
            RawWallpaper("https://images.unsplash.com/photo-1441974231531-c6227db76b6e", "Sunlit Redwood Path", "Nature", "John Salzarulo", 203),
            RawWallpaper("https://images.unsplash.com/photo-1472214222541-d510753a4707", "Serengeti Sunset", "Nature", "Sven-Erik Arndt", 412),
            RawWallpaper("https://images.unsplash.com/photo-1447752875215-b2761acb3c5d", "Ancient Forest Stream", "Nature", "Sebastian Unrau", 256),
            RawWallpaper("https://images.unsplash.com/photo-1469474968028-56623f02e42e", "Epic Mountain View", "Nature", "David Marcu", 498),
            RawWallpaper("https://images.unsplash.com/photo-1506744038136-46273834b3fb", "Yosemite Mirror Lake", "Nature", "Ansel Adams", 780),
            RawWallpaper("https://images.unsplash.com/photo-1475924156734-496f6cac6ec1", "Ocean Horizon Rise", "Nature", "Quenten Janssen", 312),
            RawWallpaper("https://images.unsplash.com/photo-1461896836934-ffe607ba8211", "Misty Highlands", "Nature", "Kalen Emsley", 145),
            RawWallpaper("https://images.unsplash.com/photo-1518495973542-4542c06a5843", "Golden Fern Close-up", "Nature", "Harvey L.", 189),
            RawWallpaper("https://images.unsplash.com/photo-1426604966848-d7adac402bff", "Alps Glacier Valley", "Nature", "Sven Scheuermeier", 521),
            RawWallpaper("https://images.unsplash.com/photo-1433832597046-4f10e10ac764", "Icelandic Black Beach", "Nature", "Jonatan Pie", 419),
            RawWallpaper("https://images.unsplash.com/photo-1510784722466-f2aa9c52ffe6", "Fields of Gold", "Nature", "Sasha Freemind", 380),
            RawWallpaper("https://images.unsplash.com/photo-1414235077428-338989a2e8c0", "Deep Jungle Canopy", "Nature", "Zbynek Burival", 620),
            RawWallpaper("https://images.unsplash.com/photo-1502082553048-f009c37129b9", "Ancient Giant Sequoia", "Nature", "Veeterzy", 442),
            RawWallpaper("https://images.unsplash.com/photo-1470240731273-7821a6eeb6bd", "Spring Meadow Bloom", "Nature", "Sina Dadaras", 215),
            RawWallpaper("https://images.unsplash.com/photo-1464822759023-fed622ff2c3b", "Distant Ridge Peaks", "Nature", "Benjamin Voros", 605),
            RawWallpaper("https://images.unsplash.com/photo-1513836279014-a89f7a76ae86", "Deep Forest Fog", "Nature", "Michael Ostermann", 530),
            RawWallpaper("https://images.unsplash.com/photo-1500530855697-b586d89ba3ee", "Sunset Over Crater Lake", "Nature", "Clay Banks", 310),
            RawWallpaper("https://images.unsplash.com/photo-1454496522488-7a8e488e8606", "Snow Capped Alps", "Nature", "Krzysztof Kotkowicz", 470),

            // Abstract (21-40)
            RawWallpaper("https://images.unsplash.com/photo-1541701494587-cb58502866ab", "Psychedelic Liquidity", "Abstract", "Joel Filipe", 650),
            RawWallpaper("https://images.unsplash.com/photo-1550684848-fac1c5b4e853", "Black Onyx Waves", "Abstract", "Pawel Czerwinski", 523),
            RawWallpaper("https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe", "Retro Neon Flow", "Abstract", "Saba", 290),
            RawWallpaper("https://images.unsplash.com/photo-1541701494587-cb58502866ab", "Deep Sea Fluidity", "Abstract", "Joel Filipe", 412),
            RawWallpaper("https://images.unsplash.com/photo-1518531933037-91b2f5f229cc", "Emerald Marble Texture", "Abstract", "Fakurian Design", 318),
            RawWallpaper("https://images.unsplash.com/photo-1508739773434-c26b3d09e071", "Copper Foil Ripples", "Abstract", "Pawel Czerwinski", 951),
            RawWallpaper("https://images.unsplash.com/photo-1561715276-a2d087060f1d", "Cosmic Dust Explosion", "Abstract", "AltumCode", 870),
            RawWallpaper("https://images.unsplash.com/photo-1574169208507-84376144848b", "Vibrant Swirling Liquid", "Abstract", "Fakurian Design", 610),
            RawWallpaper("https://images.unsplash.com/photo-1533090161767-e6ffed986c88", "Golden Obsidian Swirl", "Abstract", "Pawel Czerwinski", 104),
            RawWallpaper("https://images.unsplash.com/photo-1528459801416-a9e53bbf4e17", "Whispers of Paint", "Abstract", "Kseniya Lapteva", 552),
            RawWallpaper("https://images.unsplash.com/photo-1500964757637-c85e8a162699", "Prismatic Refraction", "Abstract", "Michael Dziedzic", 340),
            RawWallpaper("https://images.unsplash.com/photo-1614036417651-efe5912149d8", "Liquid Amethyst Flow", "Abstract", "Saba", 229),
            RawWallpaper("https://images.unsplash.com/photo-1618005198143-e5283b519a7f", "Aura Holography", "Abstract", "Google DeepMind", 715),
            RawWallpaper("https://images.unsplash.com/photo-1549490349-8643362247b5", "Waves of Sand", "Abstract", "Eberhard Grossgasteiger", 432),
            RawWallpaper("https://images.unsplash.com/photo-1558591710-4b4a1ae0f04d", "Sculpted Fluid Metal", "Abstract", "Joel Filipe", 889),
            RawWallpaper("https://images.unsplash.com/photo-1505904267569-f02eaeb45a4c", "Holographic Ribbons", "Abstract", "Mitch Myers", 415),
            RawWallpaper("https://images.unsplash.com/photo-1550859492-d5da9d8e45f3", "Technicolor Shards", "Abstract", "Pawel Czerwinski", 260),
            RawWallpaper("https://images.unsplash.com/photo-1579546929518-9e396f3cc809", "Vaporwave Horizon", "Abstract", "Luke Chesser", 940),
            RawWallpaper("https://images.unsplash.com/photo-1620641788421-7a1c342ea42e", "Smoky Aurora Gradient", "Abstract", "Fakurian Design", 823),
            RawWallpaper("https://images.unsplash.com/photo-1541701494587-cb58502866ab", "Obsidian & Emerald Spark", "Abstract", "Joel Filipe", 516),

            // Minimalist (41-60)
            RawWallpaper("https://images.unsplash.com/photo-1507525428034-b723cf961d3e", "Pacific Solitude", "Minimalist", "Sean Oulashin", 824),
            RawWallpaper("https://images.unsplash.com/photo-1557683316-973673baf926", "Sunset Mesh Gradient", "Minimalist", "Luke Chesser", 998),
            RawWallpaper("https://images.unsplash.com/photo-1513542789411-b6a5d4f31634", "Ethereal Pastel Circles", "Minimalist", "Faye Cornish", 377),
            RawWallpaper("https://images.unsplash.com/photo-1509114397022-ed747cca3f65", "Golden Hour Line", "Minimalist", "Jonas Allert", 432),
            RawWallpaper("https://images.unsplash.com/photo-1490730141103-6cac27aaab94", "Simple Sunrise Dot", "Minimalist", "Aleksey Kuprikov", 1250),
            RawWallpaper("https://images.unsplash.com/photo-1528459801416-a9e53bbf4e17", "Pastel Texture Void", "Minimalist", "Kseniya Lapteva", 188),
            RawWallpaper("https://images.unsplash.com/photo-1567095761054-7a02e69e5c43", "Monochrome Shadow Play", "Minimalist", "Pawel Czerwinski", 311),
            RawWallpaper("https://images.unsplash.com/photo-1500485035595-cbe6f645feb1", "Sahara Crest Line", "Minimalist", "Kalen Emsley", 780),
            RawWallpaper("https://images.unsplash.com/photo-1553356084-58ef4a67b2a7", "Soft Sage Matte", "Minimalist", "Pawel Czerwinski", 490),
            RawWallpaper("https://images.unsplash.com/photo-1505506819641-40b4d4575637", "Eclipse Crown", "Minimalist", "Mitch Myers", 612),
            RawWallpaper("https://images.unsplash.com/photo-1518156677180-95a2893f3e9f", "Morning Dew Drop", "Minimalist", "Yoki C.", 529),
            RawWallpaper("https://images.unsplash.com/photo-1541701494587-cb58502866ab", "Clean Sand Ripple", "Minimalist", "Joel Filipe", 345),
            RawWallpaper("https://images.unsplash.com/photo-1516245834210-c4c142787335", "Solitary Reed Silhouette", "Minimalist", "Eberhard Grossgasteiger", 902),
            RawWallpaper("https://images.unsplash.com/photo-1557682250-33bd709cbe85", "Deep Indigo Fill", "Minimalist", "Luke Chesser", 704),
            RawWallpaper("https://images.unsplash.com/photo-1476514525535-07fb3b4ae5f1", "Silent Nordic Calm", "Minimalist", "Jonatan Pie", 631),
            RawWallpaper("https://images.unsplash.com/photo-1500964757637-c85e8a162699", "Blush Pink Dust", "Minimalist", "Michael Dziedzic", 452),
            RawWallpaper("https://images.unsplash.com/photo-1549490349-8643362247b5", "Cream Desert Ripples", "Minimalist", "Eberhard Grossgasteiger", 801),
            RawWallpaper("https://images.unsplash.com/photo-1518531933037-91b2f5f229cc", "Green Sage Lines", "Minimalist", "Fakurian Design", 367),
            RawWallpaper("https://images.unsplash.com/photo-1512436991641-6745cdb1723f", "Wabi-Sabi Linen", "Minimalist", "Sarah Dorweiler", 298),
            RawWallpaper("https://images.unsplash.com/photo-1502082553048-f009c37129b9", "Lone Pine Outline", "Minimalist", "Veeterzy", 602),

            // Architecture (61-80)
            RawWallpaper("https://images.unsplash.com/photo-1486406146926-c627a92ad1ab", "Futuristic Glass Rise", "Architecture", "Ryunosuke Kikuno", 442),
            RawWallpaper("https://images.unsplash.com/photo-1600585154340-be6161a56a0c", "Nordic Cabin Light", "Architecture", "R-Architecture", 189),
            RawWallpaper("https://images.unsplash.com/photo-1513694203232-719a280e022f", "Monolithic Concrete Void", "Architecture", "Simone Hutsch", 112),
            RawWallpaper("https://images.unsplash.com/photo-1486406146926-c627a92ad1ab", "Skyscraper Reflections", "Architecture", "Ryunosuke Kikuno", 789),
            RawWallpaper("https://images.unsplash.com/photo-1504297050568-910d24c426d3", "Brutalist Concrete Grids", "Architecture", "Ricardo Gomez", 213),
            RawWallpaper("https://images.unsplash.com/photo-1511818966892-d7d671e672a2", "Spiral Staircase Infinity", "Architecture", "Ludwig Bieber", 1012),
            RawWallpaper("https://images.unsplash.com/photo-1545324418-cc1a3fa10c00", "Neon Cyberpunk Alley", "Architecture", "Sven Brandsma", 654),
            RawWallpaper("https://images.unsplash.com/photo-1512917774080-9991f1c4c750", "Santorini Cliff Shadows", "Architecture", "Jonathan Gallegos", 904),
            RawWallpaper("https://images.unsplash.com/photo-1486406146926-c627a92ad1ab", "Tokyo Neon Glass Lattice", "Architecture", "Ryunosuke Kikuno", 860),
            RawWallpaper("https://images.unsplash.com/photo-1600585154526-990dced4db0d", "Midcentury Modern Pool", "Architecture", "R-Architecture", 305),
            RawWallpaper("https://images.unsplash.com/photo-1518241353330-0f7941c2d9b5", "Symmetric Glass Dome", "Architecture", "Sasha Freemind", 412),
            RawWallpaper("https://images.unsplash.com/photo-1512915922686-57c11dde9b6b", "Baroque Ceiling Fresco", "Architecture", "Zoltan Tasi", 518),
            RawWallpaper("https://images.unsplash.com/photo-1490730141103-6cac27aaab94", "Golden Gate Golden Mist", "Architecture", "Aleksey Kuprikov", 1241),
            RawWallpaper("https://images.unsplash.com/photo-1545569341-9eb8b30979d9", "Kyoto Red Tori Forest", "Architecture", "Sorin Gheorghita", 980),
            RawWallpaper("https://images.unsplash.com/photo-1503899036084-c55cdd92da26", "Gothic Arch Shadows", "Architecture", "Sayan Ghosh", 321),
            RawWallpaper("https://images.unsplash.com/photo-1506973035872-a4ec16b8e8d9", "Eiffel Tower Symmetrical", "Architecture", "Chris Karidis", 850),
            RawWallpaper("https://images.unsplash.com/photo-1520250497591-112f2f40a3f4", "Brutalist Highrise Ribs", "Architecture", "Samuel Ferrara", 442),
            RawWallpaper("https://images.unsplash.com/photo-1513694203232-719a280e022f", "Architectural Shadows on Sand", "Architecture", "Simone Hutsch", 512),
            RawWallpaper("https://images.unsplash.com/photo-1504297050568-910d24c426d3", "Guggenheim Spirals", "Architecture", "Ricardo Gomez", 610),
            RawWallpaper("https://images.unsplash.com/photo-1600585154340-be6161a56a0c", "Futuristic Modular Pods", "Architecture", "R-Architecture", 712),

            // Digital Art (81-100)
            RawWallpaper("https://images.unsplash.com/photo-1579783900882-c0d3dad7b119", "Neo-Impressionist Flora", "Digital Art", "Europeana", 480),
            RawWallpaper("https://images.unsplash.com/photo-1618005198143-e5283b519a7f", "Stellar Aurora Grid", "Digital Art", "Google DeepMind", 760),
            RawWallpaper("https://images.unsplash.com/photo-1563089145-599997674d42", "Cybernetic Sakura", "Digital Art", "Daniel Olah", 310),
            RawWallpaper("https://images.unsplash.com/photo-1579783928121-7db148e147fa", "Glow Paint Synthesis", "Digital Art", "Pawel Czerwinski", 389),
            RawWallpaper("https://images.unsplash.com/photo-1550745165-9bc0b252726f", "Retro Synthwave Grid", "Digital Art", "Lorenzo Herrera", 912),
            RawWallpaper("https://images.unsplash.com/photo-1541701494587-cb58502866ab", "A.I. Neural Matrix", "Digital Art", "Joel Filipe", 889),
            RawWallpaper("https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe", "Vibrant Vapor Trails", "Digital Art", "Saba", 610),
            RawWallpaper("https://images.unsplash.com/photo-1618005198143-e5283b519a7f", "Infinite Loop Sphere", "Digital Art", "Google DeepMind", 1120),
            RawWallpaper("https://images.unsplash.com/photo-1505904267569-f02eaeb45a4c", "Holographic Ribbons", "Digital Art", "Mitch Myers", 412),
            RawWallpaper("https://images.unsplash.com/photo-1561715276-a2d087060f1d", "Galactic Nebula Brush", "Digital Art", "AltumCode", 823),
            RawWallpaper("https://images.unsplash.com/photo-1550684848-fac1c5b4e853", "Dark Fluid Matrix", "Digital Art", "Pawel Czerwinski", 498),
            RawWallpaper("https://images.unsplash.com/photo-1558591710-4b4a1ae0f04d", "Cyber-Liquid Chrome", "Digital Art", "Joel Filipe", 912),
            RawWallpaper("https://images.unsplash.com/photo-1563089145-599997674d42", "Surreal Violet Dunes", "Digital Art", "Daniel Olah", 765),
            RawWallpaper("https://images.unsplash.com/photo-1550745165-9bc0b252726f", "Sci-Fi Neon Server Room", "Digital Art", "Lorenzo Herrera", 830),
            RawWallpaper("https://images.unsplash.com/photo-1618005198143-e5283b519a7f", "Neural Waveforms Blue", "Digital Art", "Google DeepMind", 512),
            RawWallpaper("https://images.unsplash.com/photo-1567095761054-7a02e69e5c43", "Golden Spark Synthesis", "Digital Art", "Pawel Czerwinski", 423),
            RawWallpaper("https://images.unsplash.com/photo-1579546929518-9e396f3cc809", "Unicorn Pastel Gradient", "Digital Art", "Luke Chesser", 1250),
            RawWallpaper("https://images.unsplash.com/photo-1620641788421-7a1c342ea42e", "Chroma Ripple Space", "Digital Art", "Fakurian Design", 1010),
            RawWallpaper("https://images.unsplash.com/photo-1618005198143-e5283b519a7f", "Quantum Compute Matrix", "Digital Art", "Google DeepMind", 721),
            RawWallpaper("https://images.unsplash.com/photo-1563089145-599997674d42", "Hyper-Chrome Helix", "Digital Art", "Daniel Olah", 640)
        )
        return seedRaw.mapIndexed { index, raw ->
            Wallpaper(
                id = (index + 1).toString(),
                url = "${raw.url}?q=80&w=1080&auto=format&fit=crop",
                title = raw.title,
                category = raw.category,
                author = raw.author,
                downloadCount = raw.downloadCount
            )
        }
    }

    private data class RawWallpaper(
        val url: String,
        val title: String,
        val category: String,
        val author: String,
        val downloadCount: Int
    )
}
