package org.lwd.frame.util

import java.util
import javax.inject.Inject

import com.alibaba.fastjson.JSONObject
import org.springframework.stereotype.Component

import scala.xml.{Node, XML}

/**
  * @author lwd
  */
@Component("frame.util.xml")
class XmlImpl extends Xml {
    @Inject private val validator: Validator = null
    @Inject private val json: Json = null

    override def toJson(xml: String): JSONObject = {
        if (validator.isEmpty(xml))
            return null

        val json: JSONObject = new JSONObject
        toJson(json, XML.loadString(ignoreXml(xml)))
        json
    }

    private def ignoreXml(xml: String): String = {
        if (xml.startsWith("<?xml"))
            return xml.substring(xml.indexOf('>') + 1)
        xml
    }

    private def toJson(json: JSONObject, node: Node): Unit = {
        val name: String = node.label
        val obj: JSONObject = new JSONObject
        node.attributes.foreach(attribute => this.json.add(obj, attribute.key, attribute.get(attribute.key).mkString))
        var hasChild: Boolean = false
        node.child.filter(node => node.label != "#PCDATA").foreach(child => {
            hasChild = true
            toJson(obj, child)
        })
        if (!hasChild && !validator.isEmpty(node.text)) {
            if (obj.isEmpty)
                this.json.add(json, name, node.text)
            else
                this.json.add(obj, "value", node.text)
        }
        if (!obj.isEmpty)
            this.json.add(json, name, obj)
    }

    override def toMap(xml: String, root: Boolean): util.Map[String, String] = {
        if (validator.isEmpty(xml))
            return null

        val map: util.Map[String, String] = new util.HashMap[String, String]()
        val node: Node = XML.loadString(ignoreXml(xml))
        if (root) {
            toMap(map, node, "")

            return map
        }

        node.child.filter(node => node.label != "#PCDATA").foreach(child => toMap(map, child, ""))
        map
    }

    private def toMap(map: util.Map[String, String], node: Node, prefix: String): Unit = {
        val name: String = node.label
        val pre: String = prefix + name + "."
        node.attributes.foreach(attribute => map.put(pre + attribute.key, attribute.get(attribute.key).mkString))
        var hasChild: Boolean = false
        node.child.filter(node => node.label != "#PCDATA").foreach(child => {
            hasChild = true
            toMap(map, child, pre)
        })
        if (!hasChild && !validator.isEmpty(node.text))
            map.put(prefix + name, node.text)
    }
}
