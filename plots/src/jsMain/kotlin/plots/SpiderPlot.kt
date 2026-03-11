package plots

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.log
import kotlin.math.pow
import kotlin.math.roundToLong
import kotlin.math.sin
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlinx.browser.document
import org.jetbrains.letsPlot.Stat
import org.jetbrains.letsPlot.coord.coordFixed
import org.jetbrains.letsPlot.frontend.JsFrontendUtil
import org.jetbrains.letsPlot.geom.geomLabel
import org.jetbrains.letsPlot.geom.geomPath
import org.jetbrains.letsPlot.geom.geomPoint
import org.jetbrains.letsPlot.geom.geomText
import org.jetbrains.letsPlot.ggplot
import org.jetbrains.letsPlot.ggsize
import org.jetbrains.letsPlot.label.labs
import org.jetbrains.letsPlot.scale.scaleColorManual
import org.jetbrains.letsPlot.themes.elementText
import org.jetbrains.letsPlot.themes.theme
import org.jetbrains.letsPlot.themes.themeVoid
import react.FC
import react.dom.html.ReactHTML.div
import react.useEffect
import web.dom.ElementId

data class Angle(val radians: Double) {
    val sin = sin(radians)
    val cos = cos(radians)
}
/**
 * The trick is to transpose to polar manually rather than relying on coordPolar().
 * This might explain why line plots look so crazy in polar.
 * https://pyplots.ai/polar-line/letsplot
 */
@OptIn(ExperimentalUuidApi::class)
val SpiderPlot = FC<PlotProps> { props ->
    val contentId = Uuid.random().toString()
    useEffect {
        val abscissa = props.abscissa
        val ordinate = props.ordinate
        val size = abscissa.second.size.also { require(it == ordinate.second.size) { "Mismatched data sizes." } }
        val max = ordinate.second.max()
        // Extend the graph to the next unit of (base 10) magnitude of the maximum value.
        val top = floor(log(max, 10.0)).let { mag ->
            val u = 10.0.pow(mag)
            ((max + u) / u).roundToLong() * u
        }
        // Radius markers.
        val circles = Unit.let {
            val n = 4
            val dr = top / 4
            (1..n).map { it * dr }
        }.let { radii ->
            val dt = 2 * PI / CIRCLE_POINTS
            val xs = mutableListOf<Double>()
            val ys = mutableListOf<Double>()
            val rs = mutableListOf<Double>()
            radii.forEach { radius ->
                val thetas = (0 until CIRCLE_POINTS).map { i -> i * dt }.run { this + first() }
                xs.addAll(thetas.map { radius * cos(it) })
                ys.addAll(thetas.map { radius * sin(it) })
                repeat(CIRCLE_POINTS + 1) { rs.add(radius) }
            }
            mapOf("x" to xs.toList(), "y" to ys.toList(), "radius" to rs.toList())
        }
        // Radials to data points.
        val angles = (2 * PI / size).let { dt ->
            (0 until size).map { Angle(it * dt) }
        }
        val radials = Unit.let {
            val xs = mutableListOf<Double>()
            val ys = mutableListOf<Double>()
            val ts = mutableListOf<Double>()
            angles.forEach { angle ->
                xs.addAll(listOf(0.0, top * angle.cos))
                ys.addAll(listOf(0.0, top * angle.sin))
                // Yes, it must be added twice.
                repeat(2) { ts.add(angle.radians) }
            }
            mapOf("x" to xs.toList(), "y" to ys.toList(), "angle" to ts.toList())
        }
        // Labels
        val labels = angles.zip(abscissa.second).run {
            val r = 1 // max // top
            val xs = mutableListOf<Double>()
            val ys = mutableListOf<Double>()
            val ts = mutableListOf<String>()
            forEach { (angle, label) ->
                val x = r * angle.cos
                xs.add(x)
                val y = r * angle.sin
                ys.add(y)
                ts.add(label)
            }
            mapOf("x" to xs.toList(), "y" to ys.toList(), "label" to ts.toList())
        }
        // Data
        // One extra to close the loops.
        val ordinateClosed = ordinate.second + ordinate.second.first()
        val anglesClosed = angles + angles.first()
        val data = ordinateClosed.zip(anglesClosed).let {
            val xs = mutableListOf<Double>()
            val ys = mutableListOf<Double>()
            val thetas = mutableListOf<Double>()
            val radii = mutableListOf<Double>()
            val groups = mutableListOf<String>()
            it.forEach { (r, theta) ->
                xs.add(r * theta.cos)
                ys.add(r * theta.sin)
                thetas.add(theta.radians)
                radii.add(r)
                groups.add("agency")
            }
            mapOf("x" to xs.toList(), "y" to ys.toList(), "radius" to radii, "angle" to thetas, "group" to groups)
        }
        val plot = ggplot() + coordFixed() +
                geomPath(data = circles, color = "#CCCCCC", size = 0.5, alpha = 1.0) {
                    x = "x"; y = "y"; group = "radius"
                } +
                geomPath(data = radials, color = "#CCCCCC", size = 0.5, alpha = 1.0) {
                    x = "x"; y = "y"; group = "angle"
                } +
                geomPath(data = data, size = 2) {
                    x = "x"; y = "y"; group = "group"
                } +
                geomPoint(data = data, size = 5) {
                    x = "x"; y = "y"; group = "group"
                }+
                geomText(stat = Stat.identity, data = labels, size = 20, color = "#777777") {
                    x = "x"; y = "y"; group = "label"
                } +
                scaleColorManual(values = listOf("#306998", "#FFD43B")) +
                // This labs line jacks the aspect ratio, for some reason.
                // labs(title = "Spider Web Plot")
                themeVoid() +
                theme(
                    plotTitle = elementText(size = 24, hjust = 0.5),
                    legendTitle = elementText(size = 18),
                    legendText = elementText(size = 16),
                ) //+ ggsize(1600, 900)
        val plotDiv = JsFrontendUtil.createPlotDiv(plot)
        val contentDiv = document.getElementById(contentId)?.apply {
            innerHTML = ""
        } ?: error("Content DIV not found: $contentId")
        contentDiv.appendChild(plotDiv)
    }
    div { id = ElementId(contentId) }
}
private const val CIRCLE_POINTS = 100