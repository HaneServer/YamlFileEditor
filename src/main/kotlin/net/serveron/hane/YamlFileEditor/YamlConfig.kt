package net.serveron.hane.YamlFileEditor

import io.github.cdimascio.dotenv.Dotenv
import org.yaml.snakeyaml.Yaml
import java.io.FileReader

class YamlConfig {
    lateinit var root: MutableMap<String?, Any?>
    lateinit var yaml: Yaml

    fun regiMap(): Yaml {
        yaml = Yaml()
        root = yaml.load(FileReader(Dotenv.load().get("CONFIGPATH")))
        return yaml
    }

    fun setProperty(path: String, value: Any?) {
        if (!path.contains(".")) {
            root.put(path, value)
            return
        }
        val parts = path.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        var node: MutableMap<String?, Any?> = root
        for (i in parts.indices) {
            var o = node[parts[i]]

            if (i == parts.size - 1) {
                node[parts[i]] = value
                return
            }
            if (o == null || o !is Map<*, *>) {
                o = LinkedHashMap<String, Any>()
                node[parts[i]] = o
            }
            node = o as MutableMap<String?, Any?>
        }
    }

    fun getProperty(path: String): Any? {
        if (!path.contains(".")) {
            return root[path]
        }
        val parts = path.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        var node: Map<String?, Any?> = root
        for (i in parts.indices) {
            val o = node[parts[i]] ?: return null
            if (i == parts.size - 1) {
                return o
            }
            node = try {
                o as Map<String?, Any?>
            } catch (e: ClassCastException) {
                return null
            }
        }
        return null
    }
}