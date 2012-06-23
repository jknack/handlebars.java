package specs;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({CommentsTest.class, DelimitersTest.class,
    InterpolationTest.class, InvertedTest.class, SectionsTest.class,
    PartialsTest.class, PartialsNoSpecTest.class, LambdasTest.class })
public class SpecTests {

}
