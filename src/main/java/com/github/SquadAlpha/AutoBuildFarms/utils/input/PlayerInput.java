package com.github.SquadAlpha.AutoBuildFarms.utils.input;

public interface PlayerInput<T> {

    //Wait for the selection to be completed
    T await();

    //Start the selection process
    void go();
}
