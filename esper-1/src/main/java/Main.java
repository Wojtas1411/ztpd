import com.espertech.esper.common.client.EPCompiled;
import com.espertech.esper.common.client.configuration.Configuration;
import com.espertech.esper.compiler.client.CompilerArguments;
import com.espertech.esper.compiler.client.EPCompileException;
import com.espertech.esper.compiler.client.EPCompilerProvider;
import com.espertech.esper.runtime.client.*;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Configuration configuration = new Configuration();
        configuration.getCommon().addEventType(KursAkcji.class);
        EPRuntime epRuntime = EPRuntimeProvider.getDefaultRuntime(configuration);
//        29
//        "select istream data, spolka, kursOtwarcia-max(kursOtwarcia) as roznica "
//                + "from KursAkcji(spolka='Oracle').win:length(5)"

        EPDeployment deployment = compileAndDeploy(epRuntime,
                "select istream data, spolka, kursOtwarcia-min(kursOtwarcia) as roznica "
                + "from KursAkcji(spolka='Oracle').win:length(2) having min(kursOtwarcia) < kursOtwarcia"
        );

        ProstyListener listener = new ProstyListener();

        for (EPStatement statement : deployment.getStatements()) {
            statement.addListener(listener);
        }

        InputStream inputStream = new InputStream();
        inputStream.generuj(epRuntime.getEventService());

    }

    public static EPDeployment compileAndDeploy(EPRuntime epRuntime, String epl) {
        EPDeploymentService deploymentService = epRuntime.getDeploymentService();
        CompilerArguments args = new CompilerArguments(epRuntime.getConfigurationDeepCopy());
        EPDeployment deployment;
        try {
            EPCompiled epCompiled = EPCompilerProvider.getCompiler().compile(epl, args);
            deployment = deploymentService.deploy(epCompiled);
        } catch (EPCompileException e) {
            throw new RuntimeException(e);
        } catch (EPDeployException e) {
            throw new RuntimeException(e);
        }
        return deployment;
    }
}
