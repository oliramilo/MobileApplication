package com.curtin.mathtest.Fragments;

public interface FragmentUpdater<E> {
    public void updateFragment(E object);
    public void insertData(E data);
}
