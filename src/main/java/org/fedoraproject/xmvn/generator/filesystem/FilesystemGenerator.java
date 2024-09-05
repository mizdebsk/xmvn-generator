package org.fedoraproject.xmvn.generator.filesystem;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.fedoraproject.xmvn.generator.BuildContext;
import org.fedoraproject.xmvn.generator.Collector;
import org.fedoraproject.xmvn.generator.Generator;

class FilesystemGenerator implements Generator {
    private final BuildContext context;

    public FilesystemGenerator(BuildContext context) {
        this.context = context;
    }

    @Override
    public void generate(List<Path> filePaths, Collector collector) {
        Path buildRoot = Path.of(context.eval("%{buildroot}"));
        List<Path> prefixes = new ArrayList<>();
        prefixes.add(buildRoot.resolve("etc/java"));
        prefixes.add(buildRoot.resolve("etc/jvm"));
        prefixes.add(buildRoot.resolve("usr/lib/eclipse"));
        prefixes.add(buildRoot.resolve("usr/lib/java"));
        prefixes.add(buildRoot.resolve("usr/lib/jvm"));
        prefixes.add(buildRoot.resolve("usr/lib/jvm-common"));
        prefixes.add(buildRoot.resolve("usr/lib/jvm-private"));
        prefixes.add(buildRoot.resolve("usr/share/eclipse"));
        prefixes.add(buildRoot.resolve("usr/share/ivy-xmls"));
        prefixes.add(buildRoot.resolve("usr/share/java"));
        prefixes.add(buildRoot.resolve("usr/share/javadoc"));
        prefixes.add(buildRoot.resolve("usr/share/jvm"));
        prefixes.add(buildRoot.resolve("usr/share/jvm-common"));
        prefixes.add(buildRoot.resolve("usr/share/maven-metadata"));
        prefixes.add(buildRoot.resolve("usr/share/maven-poms"));
        for (Path filePath : filePaths) {
            for (Path prefix : prefixes) {
                if (filePath.getParent().startsWith(prefix)) {
                    collector.addRequires(filePath, "javapackages-filesystem");
                    break;
                }
            }
        }
    }

    @Override
    public String toString() {
        return "javapackages-filesystem generator";
    }
}