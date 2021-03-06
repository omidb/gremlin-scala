package gremlin.scala

import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph
import org.scalatest.matchers.ShouldMatchers
import org.apache.tinkerpop.gremlin.structure.T

class ElementSpec extends TestBase {

  describe("properties") {
    it("gets properties") {
      v(1).keys should be(Set("name", "age"))
      v(1).property[String]("name").value should be("marko")
      v(1).property[String]("doesnt exit").isPresent should be(false)
      v(1).propertyMap() should be(Map("name" -> "marko", "age" -> 29))
      v(1).propertyMap("name", "age") should be(Map("name" -> "marko", "age" -> 29))
      v(1).properties("name", "age") should have length (2)
      v(1).properties() should have length (2)

      e(7).keys should be(Set("weight"))
      e(7).property[Float]("weight").value should be(0.5)
      e(7).property[Float]("doesnt exit").isPresent should be(false)
      e(7).propertyMap("weight") should be(Map("weight" -> 0.5))
    }

    it("sets a property") {
      v(1).setProperty("vertexProperty", "updated")
      v(1).property[String]("vertexProperty").value should be("updated")

      e(7).setProperty("edgeProperty", "updated")
      e(7).property[String]("edgeProperty").value should be("updated")
    }

    it("removes a property") {
      v(1).setProperty("vertexProperty1", "updated")
      v(1).removeProperty("vertexProperty1")
      v(1).removeProperty("doesnt exist")
      v(1).property[String]("vertexProperty1").isPresent should be(false)

      e(7).setProperty("edgeProperty", "updated")
      e(7).removeProperty("edgeProperty")
      e(7).removeProperty("doesnt exist")
      e(7).property[String]("edgeProperty").isPresent should be(false)
    }
  }

  describe("values") {
    it("gets a value") {
      v(1).value[String]("name") shouldBe Some("marko")
      v(1).value[String]("doesn't exist") shouldBe None
      v(1).getValue[String]("name") shouldBe "marko"
      e(7).value[Float]("weight") shouldBe Some(0.5)
    }

    it("falls back to default value if value doesnt exist") {
      v(1).valueOrElse("doesnt exist", "blub") should be("blub")
      e(7).valueOrElse("doesnt exist", 0.8) should be(0.8)
    }

    it("returns None if it doesn't exist") {
      v(1).value[String]("doesnt exit") shouldBe None
      e(7).value[Float]("doesnt exit") shouldBe None
    }
  }

  describe("id, equality and hashCode") {
    it("has an id") {
      v(1).id should be(1)
      e(7).id should be(7)
    }

    it("equals") {
      v(1) == v(1) should be(true)
      v(1) == v(2) should be(false)
    }

    it("uses the right hashCodes") {
      v(1).hashCode should be(v(1).hashCode)
      v(1).hashCode should not be (v(2).hashCode)

      Set(v(1)) contains (v(1)) should be(true)
      Set(v(1)) contains (v(2)) should be(false)
    }
  }

  describe("adding and removing elements") {

    it("adds a vertex") {
      val gs: ScalaGraph = GremlinScala(TinkerGraph.open)
      val v1 = gs.addVertex()
      val v2 = gs.addVertex()
      v2.setProperty("testkey", "testValue")

      gs.v(v1.id) should be(Some(v1))
      gs.v(v2.id).get.property[String]("testkey").value should be("testValue")
      gs.V.toList.size should be(2)
    }

    it("adds a vertex with a given label") {
      val gs: ScalaGraph = GremlinScala(TinkerGraph.open)
      val label1 = "label1"
      val label2 = "label2"
      val v1 = gs.addVertex(label1)
      val v2 = gs.addVertex(label2, Map("testkey" → "testValue"))

      gs.V.has(T.label, label1).head shouldBe v1.vertex
      gs.V.has(T.label, label2).head shouldBe v2.vertex
      gs.V.has(T.label, label2).head.value[String]("testkey") shouldBe "testValue"
    }

    it("adds an edge") {
      val gs: ScalaGraph = GremlinScala(TinkerGraph.open)
      val v1 = gs.addVertex()
      val v2 = gs.addVertex()

      val e = v1.addEdge("testLabel", v2, Map.empty)
      e.label should be("testLabel")
      v1.outE.head should be(e.edge)
      v1.out("testLabel").head should be(v2.vertex)
    }

    it("adds an edge with additional properties") {
      val gs: ScalaGraph = GremlinScala(TinkerGraph.open)
      val v1 = gs.addVertex()
      val v2 = gs.addVertex()

      val e = v1.addEdge("testLabel", v2, Map("testKey" -> "testValue"))
      e.label should be("testLabel")
      e.propertyMap("testKey") should be(Map("testKey" -> "testValue"))
      v1.outE.head should be(e.edge)
      v1.out("testLabel").head should be(v2.vertex)
    }

    it("removes elements") {
      val gs: ScalaGraph = GremlinScala(TinkerGraph.open)
      val v = gs.addVertex()
      v.remove()
      gs.V.toList.size should be(0)
    }

  }

}

