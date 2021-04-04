package com.cqrs.messaging;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public final class ActionHandlerResolver {

    private Set<ActionHandler> actionHandlers;

    public void setActionHandlers(Set<ActionHandler> actionHandlers) {
        this.actionHandlers = new HashSet<>(actionHandlers);
    }

    public void registerActionHandler(ActionHandler... actionHandlers) {
        for (ActionHandler handler : actionHandlers) {
            this.registerActionHandler(handler);
        }
    }

    public <T extends ActionHandler> void registerActionHandler(T actionHandler) {
        if (actionHandlers == null) {
            actionHandlers = new HashSet<>();
        }

        actionHandlers.add(actionHandler);
    }

    public List<String> getSupportedActions() {
        List<String> actions = new ArrayList<>();
        for (ActionHandler actionHandler : this.actionHandlers) {
            Class<?> actionClass = getHandledActionType(actionHandler.getClass());

            if (actionClass != null) {
                String action = actionClass.getSimpleName();
                actions.add(action);
            }
        }

        return actions;
    }

    public List<ActionHandler> findHandlersFor(String action) {
        return actionHandlers.stream()
                .filter(actionHandler -> getHandledActionType(actionHandler.getClass()).getSimpleName().equals(action))
                .collect(Collectors.toList());
    }

    public Class<?> getHandledActionType(Class<?> clazz) {
        Type[] genericInterfaces = clazz.getGenericInterfaces();

        Optional<ParameterizedType> type = findByRawType(genericInterfaces, CommandHandler.class);
        type = type.map(Optional::of).orElse(findByRawType(genericInterfaces, EventHandler.class));
        // Java 9 supports type.or(() -> findByRawType(genericInterfaces, EventHandler.class));

        return getClass(type.isPresent() ? type.get() : null);
    }

    private static Class<?> getClass(ParameterizedType type) {
        if (type != null) {
            return (Class<?>) type.getActualTypeArguments()[0];
        }

        return null;
    }

    private Optional<ParameterizedType> findByRawType(Type[] genericInterfaces, Class<?> expectedRawType) {
        for (Type type : genericInterfaces) {
            if (type instanceof ParameterizedType) {
                ParameterizedType parametrized = (ParameterizedType) type;

                if (expectedRawType.equals(parametrized.getRawType())) {
                    return Optional.of(parametrized);
                }
            }
        }

        return Optional.empty();
    }
}
