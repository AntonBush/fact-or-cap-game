package com.tmvlg.factorcapgame.data.repository.fact.asli

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode

class AsliFactJsonDeserializer : JsonDeserializer<AsliFact>() {
    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): AsliFact {
        if (p == null) {
            throw NullPointerException("parser is null")
        }

        // {
        //     "status": true,
        //     "data": {
        //         "id": "48",
        //         "fact": "The world\u2019s longest mountain chain is underwater.",
        //         "cat":"sea",
        //         "hits":"292"
        //     }
        // }
        val root = p.codec.readTree<ObjectNode>(p)
        val data = root["data"] as ObjectNode
        val fact = (data["fact"] as TextNode).textValue()
        val isTrue = true

        return AsliFact(isTrue, fact)
    }
}