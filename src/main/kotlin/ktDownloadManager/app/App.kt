package ktDownloadManager.app

import tornadofx.App
import tornadofx.View
import tornadofx.button
import tornadofx.label
import tornadofx.vbox

class MyApp : App(MyView::class) {

}

class MyView : View() {
    override val root = vbox {
        button("Press me")
        label("Waiting")
    }
}