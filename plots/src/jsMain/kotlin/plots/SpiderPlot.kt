package plots

import kotlin.math.PI
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.log
import kotlin.math.pow
import kotlin.math.sin
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlinx.browser.document
import org.jetbrains.letsPlot.frontend.JsFrontendUtil
import org.jetbrains.letsPlot.geom.geomPath
import org.jetbrains.letsPlot.ggplot
import react.FC
import react.dom.html.ReactHTML.div
import react.useEffect
import web.dom.ElementId

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
        val size = abscissa.second.size.also { require(it == ordinate.second.size) { "ueonteuhontehuo" } }
        // One extra to close the loop.
        val angles = (2 * PI / size).let { dt ->
            (0 until size).map { it * dt }
        }
        val anglesClosed = angles + angles.first()
        val abscissaClosed = abscissa.first to abscissa.second + abscissa.second.first()
        val ordinateClosed = ordinate.first to ordinate.second + ordinate.second.first()
        val polar = ordinateClosed.second.zip(anglesClosed).map { (r, theta) ->
            val x = r * cos(theta)
            val y = r * sin(theta)
        }
        val max = ordinate.second.max()
        // Draw the circles as a data set.
        val circles = ordinate.second.max().let { max ->
            val top = 10.0.pow(ceil(log(max, 10.0)))
            val n = 4
            val dr = top / 4
            (1 .. n).map { it * dr }
        }.let { radii ->
            val dt = 2 * PI / CIRCLE_POINTS
            val xs = mutableListOf<Double>()
            val ys = mutableListOf<Double>()
            val rs = mutableListOf<Double>()
            radii.forEach { radius ->
                val thetas = (0 until CIRCLE_POINTS).map { i -> i * dt }
                xs.addAll(thetas.map { radius * cos(it) })
                ys.addAll(thetas.map { radius * sin(it) })
                repeat(CIRCLE_POINTS) { rs.add(radius) }
            }
            mapOf("x" to xs, "y" to ys, "radius" to rs)
        }
//        val radials = angles.map
        val plot = ggplot() +
                geomPath(
                    data = circles,
                    color = "#CCCCCC",
                    size = 0.5,
                    alpha = 0.6
                ) {
                    x = "x"
                    y = "y"
                    group = "radius"
                }
//        val data = mapOf(abscissaClosed, ordinateClosed)
//        val plot = letsPlot(data) +
//                theme(
//                    panelGridMinorY = elementLine(color = Color.BLACK)
//                ) +
//                coordPolar() +
//                geomBar(
//                    stat = Stat.identity,
//                    color = "dark-green",
//                    fill = "green",
//                    alpha = .3,
//                    size = 1.0
//                ) {
//                    x = abscissa.first
//                    y = ordinate.first
//                }.let {
//                    if (null == props.scaleY) it
//                    else it + (props.scaleY as Feature)
//                }
        val plotDiv = JsFrontendUtil.createPlotDiv(plot)
        val contentDiv = document.getElementById(contentId)?.apply {
            innerHTML = ""
        } ?: error("Content DIV not found: $contentId")
        contentDiv.appendChild(plotDiv)
    }
    div { id = ElementId(contentId) }
}
private const val CIRCLE_POINTS = 100