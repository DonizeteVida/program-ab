package parser.xml

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root
data class Aiml(
    @ElementList(name = "category", inline = true)
    @field:ElementList(name = "category", inline = true)
    val categories: List<Category>
)

@Root
data class Category(
    @Element(name = "pattern")
    @field:Element(name = "pattern")
    val pattern: String,
    @Element(name = "template")
    @field:Element(name = "template")
    val template: String
)