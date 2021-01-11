package ktDownloadManager.app.view

import javafx.beans.property.SimpleStringProperty
import tornadofx.*

class TextView : View() {
    val input = SimpleStringProperty()

    override val root = form {
        fieldset {
            field("Input") {
                textfield(input)
            }

            button("Commit") {
                action {
                    input.value = ""
                }
            }
        }
    }
}