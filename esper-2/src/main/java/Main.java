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

        String q1 = "select irstream data, spolka, max(kursZamkniecia) "
                + "from KursAkcji(spolka='Oracle').win:ext_timed(data.getTime(), 7 days)";

        String q2 = "select irstream data, kursZamkniecia, max(kursZamkniecia) "
                + "from KursAkcji(spolka = 'Oracle').win:ext_timed_batch(data.getTime(), 7 days)";

        String t5 = "select istream data, kursZamkniecia, spolka, max(kursZamkniecia)-kursZamkniecia as roznica "
                + "from KursAkcji.win:ext_timed_batch(data.getTime(), 1 day)";

        String t6 = "select istream data, kursZamkniecia, spolka, max(kursZamkniecia)-kursZamkniecia as roznica "
                + "from KursAkcji(spolka = 'Honda' or spolka = 'IBM' or spolka='Microsoft').win:ext_timed_batch(data.getTime(), 1 day)";

        String t7a = "select istream data, kursZamkniecia, kursOtwarcia, spolka "
                + "from KursAkcji.win:ext_timed(data.getTime(), 1 day) where kursZamkniecia>kursOtwarcia";

        String t7b = "select istream data, kursZamkniecia, kursOtwarcia, spolka "
                + "from KursAkcji.win:ext_timed(data.getTime(), 1 day) where KursAkcji.czyKursZamknieciaWikeszy(kursZamkniecia,kursOtwarcia)";

        String t8 = "select istream data, kursZamkniecia, spolka, max(kursZamkniecia)-kursZamkniecia as roznica "
                + "from KursAkcji(spolka in ('CocaCola', 'PepsiCo')).win:ext_timed(data.getTime(), 7 days)";

        String t9 = "select istream data, kursZamkniecia, spolka "
                + "from KursAkcji(spolka in ('CocaCola', 'PepsiCo')).win:ext_timed_batch(data.getTime(), 1 day) having max(kursZamkniecia) = kursZamkniecia";

        String t10 = "select istream max(kursZamkniecia) as maksimum "
                + "from KursAkcji.win:ext_timed_batch(data.getTime(), 7 days)";

        String t11 = "select istream cc.kursZamkniecia as kursCC, pc.kursZamkniecia as kursPC ,pc.data "
                + "from KursAkcji(spolka='CocaCola').win:length(1) as cc join KursAkcji(spolka='PepsiCo').win:length(1) as pc on cc.data = pc.data where pc.kursZamkniecia > cc.kursZamkniecia";

        String t12 = "select istream current.data, current.spolka, current.kursZamkniecia, current.kursZamkniecia - fi.kursZamkniecia as roznica "
                + "from KursAkcji(spolka in ('CocaCola', 'PepsiCo')).win:length(1) as current "
                + "join KursAkcji(spolka in ('CocaCola', 'PepsiCo')).std:firstunique(spolka) as fi on current.spolka = fi.spolka ";

        String t13 = "select istream current.data, current.spolka, current.kursZamkniecia, current.kursZamkniecia - fi.kursZamkniecia as roznica "
                + "from KursAkcji.win:length(1) as current "
                + "join KursAkcji.std:firstunique(spolka) as fi on current.spolka = fi.spolka where current.kursZamkniecia > fi.kursZamkniecia";

        String t14 = "select istream A.data, B.data, A.spolka, A.kursOtwarcia, B.kursOtwarcia "
                + "from KursAkcji.win:ext_timed(data.getTime(), 7 days) as A "
                + "join KursAkcji.win:ext_timed(data.getTime(), 7 days) as B on A.spolka = B.spolka where  B.kursOtwarcia - A.kursOtwarcia > 3";

        String t15 = "select istream data, spolka, obrot "
                + "from KursAkcji(market='NYSE').win:ext_timed_batch(data.getTime(), 7 days) "
                + "order by obrot desc limit 3";

        String t16 = "select istream data, spolka, obrot "
                + "from KursAkcji(market='NYSE').win:ext_timed_batch(data.getTime(), 7 days) "
                + "order by obrot desc limit 1 offset 2";

        EPDeployment deployment = compileAndDeploy(epRuntime, t16);

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
