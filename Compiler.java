import errors.ErrorHandler;
import intermediatecode.Translator;
import intermediatecode.codes.IntermediateCodeList;
import lexer.Lexer;
import mips.Generator;
import mips.instructions.types.oneregimmes.Li;
import optimizers.Optimizer;
import optimizers.livevar.LiveVarOptimizer;
import optimizers.peephole.PeepHoleOptimizer;
import optimizers.propagation.DefOptimizer;
import optimizers.publicmerge.MergeOptimizer;
import parser.Parser;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class Compiler {
    public static void main(String[] args) throws IOException {
        final InputStream input = new FileInputStream("testfile.txt");
        final OutputStream output = new FileOutputStream("output.txt");
        final OutputStream error = new FileOutputStream("error.txt");
        Lexer lexer = new Lexer(input);
        //lexer.print(output);
        Parser parser = new Parser(lexer.getTokens());
        //parser.print(output);
        ErrorHandler errorHandler = new ErrorHandler(parser.getTree());
        if (errorHandler.hasError()) {
            errorHandler.print(error);
            return;
        }
        OutputStream intermediate = new FileOutputStream("intermediate.txt");
        Translator translator = new Translator(parser.getTree());
        translator.print(intermediate);
        IntermediateCodeList codes = translator.getIntermediateCodes();

        OutputStream optimized = new FileOutputStream("optimized.txt");
        ArrayList<Optimizer> optimizers = new ArrayList<>();
        optimizers.add(new PeepHoleOptimizer());
        optimizers.add(new MergeOptimizer());
        optimizers.add(new DefOptimizer());
        optimizers.add(new LiveVarOptimizer());
        boolean changed = true;
        while (changed) {
            changed = false;
            for (Optimizer optimizer : optimizers) {
                codes = optimizer.optimize(codes);
                if (optimizer.isChanged()) {
                    changed = true;
                }
            }
        }
        codes.print(optimized);
        
        OutputStream mips = new FileOutputStream("mips.txt");
        Generator generator = new Generator(codes);
        generator.print(mips);
        error.close();
        input.close();
        output.close();
        intermediate.close();
        optimized.close();
        mips.close();
    }
}
