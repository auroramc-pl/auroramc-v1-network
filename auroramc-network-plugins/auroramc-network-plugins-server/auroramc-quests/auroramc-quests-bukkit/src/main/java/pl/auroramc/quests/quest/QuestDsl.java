package pl.auroramc.quests.quest;

import static groovy.lang.Closure.DELEGATE_ONLY;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import java.util.ArrayList;
import java.util.List;
import pl.auroramc.quests.objective.Objective;
import pl.auroramc.quests.objective.ObjectiveBuilder;

class QuestDsl {

  QuestDsl() {}

  public static Quest quest(final @DelegatesTo(QuestBuilder.class) Closure<?> closure) {
    final QuestBuilder delegate = new QuestBuilder();
    closure.setDelegate(delegate);
    closure.setResolveStrategy(DELEGATE_ONLY);
    closure.call();
    return delegate.build();
  }

  static class ObjectivesDsl {

    private final List<Objective<?>> objectives;

    ObjectivesDsl() {
      this.objectives = new ArrayList<>();
    }

    public void objective(final @DelegatesTo(ObjectiveBuilder.class) Closure<?> closure) {
      final ObjectiveBuilder<?> delegate = new ObjectiveBuilder<>();
      closure.setDelegate(delegate);
      closure.setResolveStrategy(DELEGATE_ONLY);
      closure.call();
      objectives.add(delegate.build());
    }

    public List<Objective<?>> objectives() {
      return objectives;
    }
  }
}
