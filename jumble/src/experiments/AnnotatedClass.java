package experiments;

import com.reeltwo.jumble.annotations.TestClass;

@TestClass({"experiments.JumblerExperimentTest"})
public class AnnotatedClass {
  public int addOne(int x) {
    return x + 1;
  }
}
