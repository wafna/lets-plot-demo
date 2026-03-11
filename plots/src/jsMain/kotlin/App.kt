@file:OptIn(ExperimentalUuidApi::class)

import kotlin.uuid.ExperimentalUuidApi
import plots.BarChart
import plots.LinePlot
import plots.SpiderPlot
import plots.WedgePlot
import react.FC
import react.Props
import react.create
import react.dom.client.createRoot
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h1
import web.cssom.ClassName
import web.dom.ElementId
import web.dom.document

val data = mapOf(
    "CIA" to 7.0,
    "FBI" to 14.0,
    "NSA" to 12.0,
    "DIA" to 21.0,
    "NGA" to 5.0,
    "NRO" to 18.0,
)
/** Main component. Containing the chrome and the routing. */
val App = FC<Props> {
    div {
        className = ClassName("container")
        div {
            className = ClassName("row")
            div {
                className = ClassName("col-lg-12")
                h1 { +"Let's Plot Demo" }
            }
        }
        div {
            className = ClassName("row")
            div {
                className = ClassName("col-lg-12")
                SpiderPlot {
                    abscissa = "Agency" to data.map { it.key }
                    ordinate = "Score" to data.map { it.value }
                }
            }
        }
        div {
            className = ClassName("row")
            div {
                className = ClassName("col-lg-4")
                WedgePlot {
                    abscissa = "Agency" to data.map { it.key }
                    ordinate = "Score" to data.map { it.value }
                }
            }
            div {
                className = ClassName("col-lg-4")
                BarChart {
                    abscissa = "Agency" to data.map { it.key }
                    ordinate = "Score" to data.map { it.value }
                }
            }
            div {
                className = ClassName("col-lg-4")
                LinePlot {
                    abscissa = "Agency" to data.map { it.key }
                    ordinate = "Score" to data.map { it.value }
                }
            }
        }
    }
}

fun main() {
    document.getElementById(ElementId("root"))?.also { createRoot(it).render(App.create()) }
        ?: error("Couldn't find root container!")
}
