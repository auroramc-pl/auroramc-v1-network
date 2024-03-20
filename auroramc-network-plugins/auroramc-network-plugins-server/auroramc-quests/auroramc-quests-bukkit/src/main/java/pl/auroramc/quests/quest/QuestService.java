package pl.auroramc.quests.quest;

import static java.nio.file.Files.walk;

import groovy.lang.GroovyShell;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import pl.auroramc.quests.objective.Objective;
import pl.auroramc.quests.resource.key.ResourceKey;

class QuestService implements QuestFacade {

  private static final String OBJECTIVE_PACKAGE_NAME = "pl.auroramc.quests.objective";
  private static final String GROOVY_FILE_EXTENSION = ".groovy";
  private static final String GROOVY_DSL_INIT_PHRASE = "quest";

  private final ClassLoader pluginClassLoader;

  QuestService(final ClassLoader pluginClassLoader) {
    this.pluginClassLoader = pluginClassLoader;
  }

  @Override
  public List<Quest> discoverQuestDefinitions(final Path traversalPath) {
    final GroovyShell groovyShell = getDefaultGroovyShell();
    try (final Stream<Path> pathStream = walk(traversalPath)) {
      return pathStream
          .filter(Files::isRegularFile)
          .filter(this::isGroovyFile)
          .map(definitionPath -> parseQuestDefinition(groovyShell, definitionPath))
          .toList();
    } catch (final Exception exception) {
      throw new QuestDiscoveryException(
          "Could not discover quests in %s path, because of unexpected exception."
              .formatted(traversalPath.toString()),
          exception);
    }
  }

  private Quest parseQuestDefinition(final GroovyShell groovyShell, final Path definitionPath) {
    try (final InputStream inputStream = Files.newInputStream(definitionPath);
        final InputStreamReader reader = new InputStreamReader(inputStream)) {
      return (Quest) groovyShell.evaluate(reader);
    } catch (final Exception exception) {
      throw new QuestDiscoveryException(
          "Could not parse quests definition from %s path, because of unexpected exception."
              .formatted(definitionPath.toString()),
          exception);
    }
  }

  private GroovyShell getDefaultGroovyShell() {
    final ImportCustomizer importCustomizer = new ImportCustomizer();
    registerDefaultImports(importCustomizer);
    final CompilerConfiguration compilerConfiguration = new CompilerConfiguration();
    compilerConfiguration.addCompilationCustomizers(importCustomizer);
    return new GroovyShell(pluginClassLoader, compilerConfiguration);
  }

  private void registerDefaultImports(final ImportCustomizer importCustomizer) {
    importCustomizer.addImports(Material.class.getName());
    importCustomizer.addImports(ItemStack.class.getName());
    importCustomizer.addImports(ResourceKey.class.getName());
    importCustomizer.addStaticImport(QuestDsl.class.getName(), GROOVY_DSL_INIT_PHRASE);
    registerObjectiveImports(importCustomizer);
  }

  private void registerObjectiveImports(final ImportCustomizer importCustomizer) {
    try (final ScanResult scanResult =
        new ClassGraph()
            .enableAllInfo()
            .acceptPackages(OBJECTIVE_PACKAGE_NAME)
            .overrideClassLoaders(pluginClassLoader)
            .scan()) {
      scanResult.getSubclasses(Objective.class).stream()
          .map(ClassInfo::getName)
          .forEach(importCustomizer::addImports);
    }
  }

  private boolean isGroovyFile(final Path path) {
    return path.getFileName().toString().endsWith(GROOVY_FILE_EXTENSION);
  }
}
