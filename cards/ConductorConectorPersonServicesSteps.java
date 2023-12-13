package steps.cards;

import base.RequestManager;
import io.cucumber.java.pt.Quando;
import service.ConductorConectorService;

import java.io.IOException;

public class ConductorConectorPersonServicesSteps {

    private ConductorConectorService conductorConectorService;

    public ConductorConectorPersonServicesSteps() throws IOException {
        conductorConectorService = new ConductorConectorService();
    }

    @Quando("pesquiso os dados do usuario Meu Cartao utilizando o CPF {string}")
    public void pesquisarOsDadosDoClienteMCPeloCPF(String cpf) {
        RequestManager.shared().setResponse(conductorConectorService.getPersonDetailsByCPF(cpf));
    }

    @Quando("consulto o cliente na Conductor utilizando o cpf {string} e com a URL incorreta")
    public void consultarClienteNaConductorComURLIncorreta(String cpf) {
        RequestManager.shared().setResponse(conductorConectorService.getPersonDetailsByPassingTheWrongURL(cpf));
    }
}
