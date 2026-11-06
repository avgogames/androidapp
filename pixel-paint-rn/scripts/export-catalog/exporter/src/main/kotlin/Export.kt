import com.avgogames.pixelart.core.catalog.ArtCatalog
import java.io.File

/** Must match CELL_ALPHABET in src/core/data/codec.ts. */
const val ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-_"

/** Writes the 100 bundled artworks as compact JSON: palette hex colors plus one char per cell. */
fun main(args: Array<String>) {
    val arts = ArtCatalog.all()
    val out = StringBuilder()
    out.append("{\n  \"version\": 1,\n  \"size\": ${ArtCatalog.SIZE},\n  \"alphabet\": \"$ALPHABET\",\n  \"arts\": [\n")
    arts.forEachIndexed { i, art ->
        val palette = art.palette.joinToString(",") { "\"#%06x\"".format(it and 0xFFFFFF) }
        val cells = art.cellsCopy().joinToString("") { ALPHABET[it].toString() }
        val title = art.title.replace("\\", "\\\\").replace("\"", "\\\"")
        out.append(
            "    {\"id\":\"${art.id}\",\"title\":\"$title\",\"category\":\"${art.category.name}\"," +
                "\"width\":${art.width},\"height\":${art.height},\"palette\":[$palette],\"cells\":\"$cells\"}",
        )
        out.append(if (i == arts.lastIndex) "\n" else ",\n")
    }
    out.append("  ]\n}\n")
    File(args[0]).writeText(out.toString())
    println("Exported ${arts.size} artworks to ${args[0]}")
}
