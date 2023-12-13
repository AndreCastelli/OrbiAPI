package steps.cards;

import base.RequestManager;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import service.ConductorConectorService;

import java.io.IOException;
import java.util.List;

public class ConductorConectorAccountSteps extends ConductorConectorService {

    private ConductorConectorService conductorConectorService;

    public ConductorConectorAccountSteps() throws IOException {
        conductorConectorService = new ConductorConectorService();
    }

    @Quando("consulto o limite para a conta valida")
    public void consultoOLimiteParaAConta() {
        String externalAccountId = propertiesUtil.getPropertyByName("conductor.external.account.id");
        RequestManager.shared().setResponse(conductorConectorService.getAccountLimit(externalAccountId));
    }

    @Quando("consulto o limite para a conta {string} inexistente")
    @Quando("consulto o limite para a conta {string} invalida")
    public void consultoOLimiteParaAContaInexistente(String externalAccountId) {
        RequestManager.shared().setResponse(conductorConectorService.getAccountLimit(externalAccountId));
    }

    @Quando("consulto as contas da pessoa com ordernacao {string}")
    public void cosultoAsContasDaPessoaComOrdernacao(String sortDirection) {
        String personId = propertiesUtil.getPropertyByName("conductor_person_id");
        RequestManager.shared().setResponse(conductorConectorService.getAccountByPersonId(personId, sortDirection));
    }

    @Entao("valido que a ordernacao {string} esta correta para a listagem de contas")
    public void validoQueAOrdernacaoEstaCorretaParaAListagemDeContas(String sortDirection) {
        List<Integer> listaIdContas = RequestManager.shared().getResponse().getBody().jsonPath().getList("content.id");
        conductorConectorService.validateSortDirection(listaIdContas, sortDirection);
    }

    @Quando("consulto os detalhes da conta {string}")
    public void consultoOsDetalhesDaConta(String externalAccountId) {
        RequestManager.shared().setResponse(conductorConectorService.getAccountDetails(externalAccountId));
    }
}
