//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.zeroturnaround.javarebel.conf;

import com.xin.component.InitStartupActivity;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;

public class RebelPropertyResolver {
    private static final Logger log = LoggerFactory.getLogger("Core");
    private final Properties properties = new Properties();

    public RebelPropertyResolver() {

        this.load();
    }

    public String getSystemProperty(String key) {
        return System.getProperty(key);
    }

    public Properties getSystemProperties() {
        return System.getProperties();
    }

    public void checkCanWritePropertyFile() {
        File file = this.getPropertiesFile();
        if (file.exists() && !file.canWrite()) {
            throw new RuntimeException(this.getPropertiesFile() + " is not writable.");
        }
    }

    public synchronized String getProperty(String key) {
        String value = this.getSystemProperty(key);
        if (value == null && this.properties != null) {
            value = this.properties.getProperty(key);
        }

        return value == null ? null : value.trim();
    }

    public synchronized void remove(String name) {
        this.properties.remove(name);
    }

    public synchronized void add(String property, String value) {
        this.properties.put(property, value);
    }

    public synchronized void load() {
        try {
            store("初始化");
        } catch (IOException e) {
            e.printStackTrace();
        }

        File file = this.getPropertiesFile();
        if (!file.exists()) {
            log.info("No jrebel.properties found from file system.");
        } else {
            BufferedInputStream in = null;

            try {
                in = new BufferedInputStream(new FileInputStream(file));
                this.properties.clear();
                this.properties.load(in);
                InitStartupActivity.initProperty(this.properties);
                this.log("Rebel properties loaded from " + file.toString() + ":", this.properties);
            } catch (IOException var7) {
                throw new RuntimeException("Could not load properties from '" + file + "'", var7);
            } finally {
                IOUtils.closeQuietly(in);
            }

        }

    }

    public synchronized void store(String comment) throws IOException {
        InitStartupActivity.initProperty(this.properties);
        File confFile = this.getPropertiesFile();
        FileOutputStream fos = null;

        try {
            this.properties.setProperty("rebel.properties.version", "2");
            if (!confFile.getParentFile()
                         .exists()) {
                confFile.getParentFile()
                        .mkdir();
                confFile.createNewFile();
            } else if (!confFile.exists()) {
                confFile.createNewFile();
            }

            fos = new FileOutputStream(confFile);
            this.properties.store(fos, comment);
        } finally {
            IOUtils.closeQuietly(fos);
        }

    }

    public synchronized Properties getFileProperties() {
        Properties props = new Properties();
        props.putAll(this.properties);
        return props;
    }

    public Properties getProperties() {
        Properties props = this.getFileProperties();
        props.putAll(this.getSystemProperties());
        return props;
    }

    private File getPropertiesFile() {
        String propertiesFilePath = this.getSystemProperty("rebel.properties");
        if (propertiesFilePath != null) {
            File propertiesFile = new File(propertiesFilePath);
            if (propertiesFile.exists()) {
                log.info("The {} file does not exist at the specified path: {} ", "jrebel.properties", propertiesFilePath);
            }

            return propertiesFile;
        } else {
            return new File(RebelFileLayout.getPreferredMetaDataDir(), "jrebel.properties");
        }
    }

    private void log(String message, Map<Object, Object> props) {
        if (log.isInfoEnabled()) {
            try {
                StringWriter writer = new StringWriter();
                BufferedWriter out = new BufferedWriter(writer);
                out.write(message);
                out.newLine();
                Map<Object, Object> map = new TreeMap(props);
                Iterator var6 = map.entrySet()
                                   .iterator();

                while (var6.hasNext()) {
                    Entry<Object, Object> e = (Entry) var6.next();
                    out.write(e.getKey() + "=" + e.getValue());
                    out.newLine();
                }

                out.flush();
                log.info(writer.getBuffer()
                               .toString());
            } catch (Throwable var8) {
                log.error(var8.getLocalizedMessage());
            }
        }

    }
}
