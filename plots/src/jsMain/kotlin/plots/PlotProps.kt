package plots

import org.jetbrains.letsPlot.intern.Scale
import react.Props

external interface PlotProps : Props {
    var abscissa: Pair<String, List<String>>
    var ordinate: Pair<String, List<Double>>
    var scaleY: Scale?
}

