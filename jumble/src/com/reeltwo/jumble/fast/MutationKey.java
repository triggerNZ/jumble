package com.reeltwo.jumble.fast;

/**
 * This class holds the values for the test to each mutation point.
 * 
 * @author Celia Lai
 * @version $Revision: 743 $
 */
public class MutationKey {
  private String mClassName;

  private String mMethodName;

  private String mTestClassName;

  private String mTestMethodName;

  private int mMutationPoint;

  private String mMutationDescription;

  public MutationKey(String className, String testClassName, String testMethodName, String mutationDescription) {
    this.mClassName = className;
    this.mTestClassName = testClassName;
    this.mTestMethodName = testMethodName;
    this.mMutationDescription = mutationDescription;
  }

  public MutationKey(String className, String methodName, String testClassName,
      String testMethodName, int mutationPoint, String mutationDescription) {
    this(className, testClassName, testMethodName, mutationDescription);
    this.mMethodName = methodName;
    this.mMutationPoint = mutationPoint;
  }

  public String getClassName() {
    return mClassName;
  }

  public void setClassName(String className) {
    this.mClassName = className;
  }

  public String getMethodName() {
    return mMethodName;
  }

  public void setMethodName(String methodName) {
    this.mMethodName = methodName;
  }

  public String getTestClassName() {
    return mTestClassName;
  }

  public void setTestClassName(String testClassName) {
    this.mTestClassName = testClassName;
  }

  public String getTestMethodName() {
    return mTestMethodName;
  }

  public void setTestMethodName(String testMethodName) {
    this.mTestMethodName = testMethodName;
  }

  public int getMutationPoint() {
    return mMutationPoint;
  }

  public void setMutationPoint(int mutationPoint) {
    this.mMutationPoint = mutationPoint;
  }

  public String getMutationDescription() {
    return mMutationDescription;
  }

  public void setMutationDescription(String mutationDescription) {
    this.mMutationDescription = mutationDescription;
  }

  @Override
  public boolean equals(Object other) {
    if (this == null) {
      return super.equals(other);
    }

    if (other == null) {
      return false;
    }

    if (!(other instanceof MutationKey)) {
      return false;
    }

    MutationKey mutationKey = (MutationKey) other;
    if (this.mClassName.equals(mutationKey.getClassName()) 
        && this.mTestClassName.equals(mutationKey.getTestClassName())
        && this.mTestMethodName.equals(mutationKey.getTestMethodName())
        && this.mMutationDescription.equals(mutationKey.getMutationDescription())) {
      return true;
    }

    return false;
  }

  @Override
  public int hashCode() {
    if (this == null) {
      return super.hashCode();
    }

    return mClassName.hashCode() + mTestClassName.hashCode() + 
    mTestMethodName.hashCode() + mMutationDescription.hashCode();
  }
}
