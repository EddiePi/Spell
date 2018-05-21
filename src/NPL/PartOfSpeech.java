package NPL;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Eddie on 2018/2/20.
 */
public class PartOfSpeech {
    public static Set<String> NONE = new HashSet<>(Arrays.asList("NN", "NNS", "NNP", "NNPS"));

    public static Set<String> VERB = new HashSet<>(Arrays.asList("VB", "VBZ", "VBP", "VBD", "VBN", "VBG"));

    public static Set<String> ADJ = new HashSet<>(Arrays.asList("JJ", "JJR", "JJS"));

    public static Set<String> ADV = new HashSet<>(Arrays.asList("RB", "RBR", "RBS", " RP"));
}
