package com.maddox.il2.game;

import java.util.ArrayList;

public class GameStateStack
{
  protected ArrayList stack = new ArrayList();

  public boolean isEmpty() {
    return this.stack.isEmpty();
  }

  public int size() {
    return this.stack.size();
  }

  public GameState change(int paramInt) {
    GameState localGameState = GameState.get(paramInt);
    if (localGameState == null) return null;
    return change(localGameState);
  }

  public GameState change(GameState paramGameState) {
    int i = this.stack.size();
    if (i == 0) {
      this.stack.add(paramGameState);
      paramGameState.enter(null);
    } else {
      GameState localGameState = (GameState)this.stack.get(i - 1);
      localGameState.leave(paramGameState);
      this.stack.set(i - 1, paramGameState);
      paramGameState.enter(localGameState);
    }
    return paramGameState;
  }

  public GameState push(int paramInt) {
    GameState localGameState = GameState.get(paramInt);
    if (localGameState == null) return null;
    return push(localGameState);
  }

  public GameState push(GameState paramGameState) {
    int i = this.stack.size();
    GameState localGameState = null;
    if (i > 0) {
      localGameState = (GameState)this.stack.get(i - 1);
      localGameState.leavePush(paramGameState);
    }
    this.stack.add(paramGameState);
    paramGameState.enterPush(localGameState);
    return paramGameState;
  }

  public GameState pop() {
    int i = this.stack.size();
    GameState localGameState1 = null;
    GameState localGameState2 = null;
    if (i > 0) {
      if (i > 1)
        localGameState2 = (GameState)this.stack.get(i - 2);
      localGameState1 = (GameState)this.stack.get(i - 1);
      localGameState1.leavePop(localGameState2);
      this.stack.remove(i - 1);
      if (localGameState2 != null)
        localGameState2.enterPop(localGameState1);
    }
    return localGameState2;
  }

  public GameState peek() {
    int i = this.stack.size();
    if (i == 0) return null;
    return (GameState)this.stack.get(i - 1);
  }
  
  public GameState getPrevious()
  {
      int i = stack.size();
      if(i == 0 || i < 2)
          return null;
      else
          return (GameState)stack.get(i - 2);
  }
}