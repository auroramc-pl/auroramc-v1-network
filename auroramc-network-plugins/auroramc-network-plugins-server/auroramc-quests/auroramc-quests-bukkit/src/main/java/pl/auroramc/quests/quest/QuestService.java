package pl.auroramc.quests.quest;

import static pl.auroramc.commons.eager.Eager.eager;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import java.nio.file.Path;
import java.util.List;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import pl.auroramc.commons.eager.Eager;
import pl.auroramc.integrations.dsl.BukkitDiscoveryService;
import pl.auroramc.quests.objective.Objective;

class QuestService extends BukkitDiscoveryService<Quest> implements QuestFacade {

  private static final String OBJECTIVE_PACKAGE_NAME = "pl.auroramc.quests.objective";
  private final ClassLoader parentClassLoader;
  private final Eager<List<Quest>> quests;

  QuestService(final ClassLoader parentClassLoader, final Path questDefinitionsPath) {
    super(parentClassLoader, Quest.class);
    this.parentClassLoader = parentClassLoader;
    this.quests = eager(() -> getElementDefinitions(questDefinitionsPath).stream().toList());
  }

  @Override
  public List<Quest> getQuests() {
    return quests.get();
  }

  @Override
  public ImportCustomizer getImportCustomizer() {
    final ImportCustomizer importCustomizer = super.getImportCustomizer();
    try (final ScanResult scanResult =
        new ClassGraph()
            .enableAllInfo()
            .acceptPackages(OBJECTIVE_PACKAGE_NAME)
            .overrideClassLoaders(parentClassLoader)
            .scan()) {
      scanResult.getSubclasses(Objective.class).stream()
          .map(ClassInfo::getName)
          .forEach(importCustomizer::addImports);
    }
    return importCustomizer;
  }
}
