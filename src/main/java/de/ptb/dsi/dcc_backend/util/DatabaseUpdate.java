package de.ptb.dsi.dcc_backend.util;

import de.ptb.dsi.dcc_backend.model.Dcc;
import de.ptb.dsi.dcc_backend.repository.DccRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Component
@AllArgsConstructor
public class DatabaseUpdate implements CommandLineRunner {
    private DccRepository dccRepository;
    @Override
    public void run(String... args) throws Exception {
        Dcc dccNpl= new Dcc();
        dccNpl.setPid("CCM.M-K1-NPL9507");
        dccNpl.setDccValid(false);
        dccNpl.setXmlBase64("7PfTDInn+94hXmnBr9D8+4x5RkNNl4E499Me3Fotq8/zvznEycz2h7vJ21SdP5an+94hXmnBr9D8+4x5RkNNl4E499Me3Fotq8/zvznEycz2h7vJ21SdP5akLhRPd4W1S79LoCvbZYh2x4t6xCnqev6S97ys4chOPgz0FePfKQos0I7+rrMSAc9+vXHmUCthFqp7FJJ7/D9bCfmdF1qkYNhtk/P5uvZ0N2zAUsiScDJA=");
        dccRepository.save(dccNpl);

        Dcc dccPTB= new Dcc();
        dccPTB.setPid("CCM.M-K1-PTB9608");
        dccPTB.setDccValid(false);
        dccPTB.setXmlBase64("jA0EAwMCxamDRMfOGV5gyZPnyX1BBPOQAE4BHbh7PfTDInn+94hXmnBr9D8+4x5RkNNl4E499Me3Fotq8/zvznEycz2h7vJ21SdP5akL7PfTDInn+94hXmnBr9D8+4x5RkNNl4E499Me3Fotq8/zvznEycz2h7vJ21SdP5as0I7+rrMSAc9+vXHmUCthFqp7FJJ7/D9bCfmdF1qkYNhtk/P5uvZ0N2zAUsiScDJA=");
        dccRepository.save(dccPTB);

        Dcc dccBIPM= new Dcc();
        dccBIPM.setPid("CCM.M-K1-BIPM9608");
        dccBIPM.setDccValid(true);
        dccBIPM.setXmlBase64("jA0EAwMCxamDRMfOGV5gyZPnyX1BBPOQAE4BHbhkLhRPd4W1S79LoCvbZYh2x4t6xCnqev6S97ys4chOPgz0FePfKQos0I7+rrMSAc9+vXHmUCthFqp7FJJ7/D9bCfmdF1qkYNhtk/P5uvZ0N2zAUs7PfTDInn+94hXmnBr9D8+4x5RkNNl4E499Me3Fotq8/zvznEycz2h7vJ21SdP5aiScDJA=");
        dccRepository.save(dccBIPM);

        Dcc dccKriss= new Dcc();
        dccKriss.setPid("CCM.M-K1-KRISS9608");
        dccKriss.setDccValid(true);
        dccKriss.setXmlBase64("jA0EAwMCxamDRMfOGV5gyZPnyX1BBPOQAE4BHbhkLhRPd4W1S79LoCvbZYh2x4t6xCnqev6S97ys4chOPgz0FePfKQos0I7+rrMSAc9+vXHmUCthFqp7FJJ7/D9bCfmdF1qkYNhtk/P5uvZ0N2zAUs7PfTDInn+94hXmnBr9D8+4x5RkNNl4E499Me3Fotq8/zvznEycz2h7vJ21SdP5aiScDJA=");
        dccRepository.save(dccKriss);
    }
}
