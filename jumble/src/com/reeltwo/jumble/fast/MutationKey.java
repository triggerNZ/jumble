package com.reeltwo.jumble.fast;

/**
 * This class holds the values for the test to each mutation point.
 * 
 * @author Celia Lai
 */
public class MutationKey {
  public String className;

  public String methodName;

  public String testClassName;

  public String testMethodName;

  public int mutationPoint;

  public String mutationDescription;

  public MutationKey(String className, String testClassName, String testMethodName, String mutationDescription) {
    this.className = className;
    this.testClassName = testClassName;
    this.testMethodName = testMethodName;
    this.mutationDescription = mutationDescription;
  }
  
  public MutationKey(String className, String methodName, String testClassName,
      String testMethodName, int mutationPoint, String mutationDescription) {
    this(className, testClassName, testMethodName, mutationDescription);
    this.methodName = methodName;
    this.mutationPoint = mutationPoint;
  }

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public String getMethodName() {
    return methodName;
  }

  public void setMethodName(String methodName) {
    this.methodName = methodName;
  }

  public String getTestClassName() {
    return testClassName;
  }

  public void setTestClassName(String testClassName) {
    this.testClassName = testClassName;
  }

  public String getTestMethodName() {
    return testMethodName;
  }

  public void setTestMethodName(String testMethodName) {
    this.testMethodName = testMethodName;
  }

  public int getMutationPoint() {
    return mutationPoint;
  }

  public void setMutationPoint(int mutationPoint) {
    this.mutationPoint = mutationPoint;
  }

  public String getMutationDescription() {
    return mutationDescription;
  }

  public void setMutationDescription(String mutationDescription) {
    this.mutationDescription = mutationDescription;
  }
  
  public boolean equals(MutationKey mutationKey) {
    if (this.className.equals(mutationKey.getClassName()) &&
        this.testClassName.equals(mutationKey.getTestClassName()) &&
        this.testMethodName.equals(mutationKey.getTestMethodName()) &&
        this.mutationDescription.equals(mutationKey.getMutationDescription())) {
      return true;
    }
    
    return false;
  }
}
