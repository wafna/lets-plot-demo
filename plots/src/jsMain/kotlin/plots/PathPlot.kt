package plots

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlinx.browser.document
import org.jetbrains.letsPlot.Stat
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.coord.coordPolar
import org.jetbrains.letsPlot.frontend.JsFrontendUtil
import org.jetbrains.letsPlot.geom.geomBar
import org.jetbrains.letsPlot.geom.geomPath
import org.jetbrains.letsPlot.intern.Feature
import org.jetbrains.letsPlot.letsPlot
import org.jetbrains.letsPlot.themes.elementLine
import org.jetbrains.letsPlot.themes.theme
import react.FC
import react.dom.html.ReactHTML.div
import react.useEffect
import web.dom.ElementId

@ExperimentalUuidApi
val PathPlot = FC<PlotProps> { props ->
    val contentId = Uuid.random().toString()
    useEffect {
        val contentDiv = document.getElementById(contentId)?.apply {
            innerHTML = ""
        } ?: error("Content DIV not found: $contentId")
        val abscissa = props.abscissa
        val ordinate = props.ordinate
        val data = mapOf(abscissa, ordinate)
        val plot = letsPlot(data) +
                theme(
                    panelGridMinorY = elementLine(color = Color.BLACK)
                ) +
                coordPolar() +
                geomPath(
                    stat = Stat.identity,
                    color = "dark-green",
                    alpha = .3,
                    size = 1.0
                ) {
                    x = abscissa.first
                    y = ordinate.first
                }.let {
                    if (null == props.scaleY) it
                    else it + (props.scaleY as Feature)
                }
        val plotDiv = JsFrontendUtil.createPlotDiv(plot)
        contentDiv.appendChild(plotDiv)
    }
    div { id = ElementId(contentId) }
}