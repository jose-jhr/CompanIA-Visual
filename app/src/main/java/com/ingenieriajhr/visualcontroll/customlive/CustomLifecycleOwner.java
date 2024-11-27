package com.ingenieriajhr.visualcontroll.customlive;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

public class CustomLifecycleOwner implements LifecycleOwner {

    private final LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);

    public CustomLifecycleOwner() {
        // Inicializa el ciclo de vida con el estado adecuado
        lifecycleRegistry.setCurrentState(Lifecycle.State.CREATED);
    }

    // Método para iniciar el ciclo de vida
    public void start() {
        lifecycleRegistry.setCurrentState(Lifecycle.State.STARTED);
    }

    // Método para detener el ciclo de vida
    public void stop() {
        lifecycleRegistry.setCurrentState(Lifecycle.State.CREATED);
    }

    @Override
    public Lifecycle getLifecycle() {
        return lifecycleRegistry;
    }
}
