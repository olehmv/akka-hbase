package scalafx

import scalafx.application.JFXApp
import scalafx.collections.ObservableBuffer
import scalafx.geometry.Pos
import scalafx.scene.control._
import scalafx.scene.layout.{BorderPane, _}
import scalafx.scene.{Node, Scene}


object Form extends JFXApp {

  private val borderStyle = "" +
    "-fx-background-color: white;" +
    "-fx-border-color: black;" +
    "-fx-border-width: 1;" +
    "-fx-border-radius: 6;" +
    "-fx-padding: 6;"

  stage = new JFXApp.PrimaryStage() {

    val panelsPane = new Pane() {
      val loginPanel = createLoginPanel()

      loginPanel.relocate(0, 0)

      children = Seq(loginPanel)
      alignmentInParent = Pos.TopLeft
    }

    title = "Form"
    scene = new Scene(400, 300) {
      root = new BorderPane() {
        center = panelsPane
      }
    }
  }


  private def createLoginPanel(): Node = {
    val toggleGroup1 = new ToggleGroup()

    val textField = new TextField() {
      prefColumnCount = 10
      promptText = "Your name"
    }

    val passwordField = new PasswordField() {
      prefColumnCount = 10
      promptText = "Your password"
    }

    val choiceBox = new ChoiceBox[String](
      ObservableBuffer(
        "English", "\u0420\u0443\u0441\u0441\u043a\u0438\u0439",
        "Fran\u00E7ais")) {
      tooltip = Tooltip("Your language")
      selectionModel().select(0)
    }

    new HBox(6) {
      children = Seq(
        new VBox(2) {
          children = Seq(textField, passwordField)
        },
        choiceBox
      )

      alignment = Pos.BottomLeft
      style = borderStyle
    }
  }

}

