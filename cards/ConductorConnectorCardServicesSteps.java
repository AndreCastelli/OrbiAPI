package steps.cards;

import base.RequestManager;
import io.cucumber.java.pt.Quando;
import service.ConductorConectorService;

import java.io.IOException;

public class ConductorConnectorCardServicesSteps {
    String externalAccountId;
    String accountIDJsonPath = "content.id[0]";
    String flagVirtualCardBlockedFalse = "false";
    String flagVirtualCardBlockedTrue = "true";
    private final ConductorConectorService conductorConectorService;

    public ConductorConnectorCardServicesSteps() throws IOException {
        conductorConectorService = new ConductorConectorService();
    }

    @Quando("executo a requisicao para criar o cartao virtual ativado para a conta criada")
    public void executoARequisicaoParaCriarOCartaoVirtualAtivadoParaAContaCriada() {
        externalAccountId = RequestManager.shared().getResponse().then().extract().path(accountIDJsonPath).toString();
        RequestManager.shared().setResponse(conductorConectorService.createConductorVirtualCard(
                flagVirtualCardBlockedFalse, externalAccountId));
    }

    @Quando("executo a requisicao para criar o cartao virtual desativado para a conta criada")
    public void executoARequisicaoParaCriarOCartaoVirtualDesativadoParaAContaCriada() {
        externalAccountId = RequestManager.shared().getResponse().then().extract().path(accountIDJsonPath).toString();
        RequestManager.shared().setResponse(conductorConectorService.createConductorVirtualCard(
                flagVirtualCardBlockedTrue, externalAccountId));
    }

    @Quando("executo a requisicao para criar o cartao virtual ativado para a conta {string}")
    public void executoARequisicaoParaCriarOCartaoVirtualAtivadoParaAConta(String accountId) {
        RequestManager.shared().setResponse(conductorConectorService.createConductorVirtualCard(
                flagVirtualCardBlockedFalse, accountId));
    }

    @Quando("executo a requisicao para o cartao virtual da conta {string} e ordernacao {string}")
    public void executoARequisicaoParaOCartaoVirtualDaContaEOrdernacao(String accountId, String sortDirection) {
        RequestManager.shared().setResponse(conductorConectorService.getCard(accountId, sortDirection));
    }
}
