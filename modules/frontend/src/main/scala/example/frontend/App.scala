package example.frontend

import com.raquo.laminar.api.L._
import com.raquo.waypoint._
import org.scalajs.dom

import ExampleRouter._

object App {

  def main(args: Array[String]): Unit = {

    // This div, its id and contents are defined in index-fastopt.html and index-fullopt.html files
    lazy val container = dom.document.getElementById("appContainer")

    lazy val appElement = {
      div(
        child.maybe <-- ExampleRouter.router.$currentPage.map {
          case HomePage => None
          case _        => Some(h3(a(navigateTo(HomePage), "Back to home")))
        },
        child <-- $selectedApp.$view
      )
    }

    // Wait until the DOM is loaded, otherwise app-container element might not exist
    renderOnDomContentLoaded(container, appElement)
  }

  def renderFlexiPage($flexiPage: Signal[FlexiCounterPage]): Div = {
    div(
      child <-- $flexiPage.map(page => FlexiRouteMaster(page.countMe, page.amount))
    )
  }

  private val $selectedApp = SplitRender(ExampleRouter.router.$currentPage)
    .collectStatic(HomePage)(renderHomePage())
    .collectStatic(TodoMvcPage)(TodoMvcApp())
    .collectSignal[FlexiCounterPage]($flexiPage => renderFlexiPage($flexiPage))
    .collectStatic(DuckCounterPage)(DuckMaster())

  private def renderHomePage(): HtmlElement = {
    div(
      h1("Laminar Examples"),
      ul(
        fontSize := "120%",
        lineHeight := "2em",
        listStyleType.none,
        paddingLeft := "0px",
        linkPages.map { page =>
          li(a(navigateTo(page), page.title))
        }
      )
    )
  }

  val linkPages: List[Page] = List(
    DuckCounterPage,
    new FlexiCounterPage("test", 1),
    TodoMvcPage
  )
}
