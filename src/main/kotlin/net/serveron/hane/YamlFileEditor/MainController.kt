package net.serveron.hane.YamlFileEditor

import com.fasterxml.jackson.annotation.JsonCreator
import com.google.gson.GsonBuilder
import io.github.cdimascio.dotenv.Dotenv
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.FileReader
import java.io.FileWriter

@RestController
class MainController {

    @RequestMapping(value = ["/set"], method = [RequestMethod.POST], produces = ["application/json"])
    fun set(@RequestBody data: SetData): String {
        val config = YamlConfig()
        val yaml = config.regiMap()
        config.setProperty(data.key, data.value)

        val fileWriter = FileWriter(Dotenv.load().get("CONFIGPATH"))
        yaml.dump(config.root, fileWriter)
        return "Complete"
    }

    @RequestMapping(value = ["/get"], method = [RequestMethod.POST], produces = ["application/json"])
    fun get(@RequestBody data: GetData): SetData {
        val config = YamlConfig()
        config.regiMap()
        val value = config.getProperty(data.key)
        return SetData(data.key, value)
    }

    @RequestMapping(value = ["/getall"], method = [RequestMethod.GET], produces = ["application/json"])
    fun getAll(): String {
        val f = File(Dotenv.load().get("CONFIGPATH"))
        val yaml = Yaml()

        val loadedYaml: Any = yaml.load(FileReader(f))
        val gson = GsonBuilder().setPrettyPrinting().create()
        val json = gson.toJson(loadedYaml, LinkedHashMap::class.java)
        return json
    }

    data class SetData @JsonCreator constructor(val key: String, val value: Any?)
    data class GetData @JsonCreator constructor(val key: String)
}