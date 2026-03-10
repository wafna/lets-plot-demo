package plots

import kotlinx.browser.document
import org.jetbrains.letsPlot.Stat
import org.jetbrains.letsPlot.frontend.JsFrontendUtil
import org.jetbrains.letsPlot.geom.geomLine
import org.jetbrains.letsPlot.intern.Feature
import org.jetbrains.letsPlot.intern.Scale
import org.jetbrains.letsPlot.letsPlot
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.useEffect
import web.dom.ElementId
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@ExperimentalUuidApi
val LinePlot = FC<PlotProps> { props ->
    val contentId = Uuid.random().toString()
    useEffect {
        val contentDiv = document.getElementById(contentId)?.apply {
            innerHTML = ""
        } ?: error("Content DIV not found: $contentId")
        val data = mapOf(props.abscissa, props.ordinate)
        val plot = letsPlot(data) + geomLine(
            stat = Stat.identity,
            color = "dark-green",
            alpha = .3,
            size = 2.5
        ) {
            x = props.abscissa.first
            y = props.ordinate.first
        }.let {
            if (null == props.scaleY) it
            else it + (props.scaleY as Feature)
        }

        val plotDiv = JsFrontendUtil.createPlotDiv(plot)
        contentDiv.appendChild(plotDiv)
    }
    div { id = ElementId(contentId) }
}

