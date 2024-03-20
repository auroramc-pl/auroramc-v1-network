package pl.auroramc.shops.shop;

import static java.nio.file.Files.walk;
import static java.util.Collections.unmodifiableSet;

import groovy.lang.GroovyShell;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;
import org.bukkit.Material;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;

class ShopService implements ShopFacade {

  private static final String GROOVY_FILE_EXTENSION = ".groovy";
  private static final String GROOVY_DSL_INIT_PHRASE = "shop";
  private final Set<Shop> parsedShops = new HashSet<>();
  private final ClassLoader pluginClassLoader;

  ShopService(final ClassLoader pluginClassLoader) {
    this.pluginClassLoader = pluginClassLoader;
  }

  @Override
  public Set<Shop> getShops() {
    return unmodifiableSet(parsedShops);
  }

  void discoverShopDefinitions(final Path traversalPath) {
    final GroovyShell groovyShell = getDefaultGroovyShell();
    try (final Stream<Path> pathStream = walk(traversalPath)) {
      pathStream
          .filter(Files::isRegularFile)
          .filter(this::isGroovyFile)
          .map(definitionPath -> parseShopDefinition(groovyShell, definitionPath))
          .forEach(parsedShops::add);
    } catch (final Exception exception) {
      throw new ShopDiscoveryException(
          "Could not discover shops in %s path, because of unexpected exception."
              .formatted(traversalPath.toString()),
          exception);
    }
  }

  private Shop parseShopDefinition(final GroovyShell groovyShell, final Path definitionPath) {
    try (final InputStream inputStream = Files.newInputStream(definitionPath);
        final InputStreamReader reader = new InputStreamReader(inputStream)) {
      return (Shop) groovyShell.evaluate(reader);
    } catch (final Exception exception) {
      throw new ShopDiscoveryException(
          "Could not parse shop definition from %s path, because of unexpected exception."
              .formatted(definitionPath.toString()),
          exception);
    }
  }

  private GroovyShell getDefaultGroovyShell() {
    final ImportCustomizer importCustomizer = new ImportCustomizer();
    importCustomizer.addImports(Material.class.getName());
    importCustomizer.addStaticImport(ShopDsl.class.getName(), GROOVY_DSL_INIT_PHRASE);
    final CompilerConfiguration compilerConfiguration = new CompilerConfiguration();
    compilerConfiguration.addCompilationCustomizers(importCustomizer);
    return new GroovyShell(pluginClassLoader, compilerConfiguration);
  }

  private boolean isGroovyFile(final Path path) {
    return path.getFileName().toString().endsWith(GROOVY_FILE_EXTENSION);
  }
}
