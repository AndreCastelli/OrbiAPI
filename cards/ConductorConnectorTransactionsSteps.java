package steps.cards;

import base.RequestManager;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import org.junit.Assert;
import service.ConductorConectorService;

import java.io.IOException;

public class ConductorConnectorTransactionsSteps extends ConductorConectorService {

    private ConductorConectorService conductorConectorService;

    public ConductorConnectorTransactionsSteps() throws IOException {
        conductorConectorService = new ConductorConectorService();
    }

    @Quando("consulta a timeline de transacoes para a conta valida")
    public void consultaATimelineDeTransacoesParaAConta() {
        String externalAccountId = propertiesUtil.getPropertyByName("conductor.external.account.id");
        RequestManager.shared().setResponse(conductorConectorService.getRecentTransactions(externalAccountId));
    }

    @Quando("consulta a timeline de transacoes para a conta {string} inexistente")
    @Quando("consulta a timeline de transacoes para a conta {string} invalida")
    public void consultaATimelineDeTransacoesParaAContaInexistente(String externalAccountId) {
        RequestManager.shared().setResponse(conductorConectorService.getRecentTransactions(externalAccountId));
    }

    @Quando("consulta a timeline de transacoes para a conta valida utilizando limite de {int} registros")
    public void consultaATimelineDe(int limit) {
        String externalAccountId = propertiesUtil.getPropertyByName("conductor.external.account.id");
        RequestManager.shared().setResponse(conductorConectorService.getRecentTransactionsByLimit(externalAccountId,
                limit));
    }

    @Entao("valido que retornou {string} registros")
    public void validoQueRetornouRegistros(String totalRegistros) {
        Assert.assertEquals(RequestManager.shared().getResponse().jsonPath().getString("numberOfElements"),
                totalRegistros);
        Assert.assertEquals(RequestManager.shared().getResponse().then().extract().jsonPath().getList("content").size(),
                Integer.parseInt(totalRegistros));
    }

    @Quando("consulta a timeline de transacoes para a conta valida utilizando consulta da pagina {int}")
    public void consultaATimelineDeTransacoesPara(Integer page) {
        String externalAccountId = propertiesUtil.getPropertyByName("conductor.external.account.id");
        RequestManager.shared().setResponse(conductorConectorService.getRecentTransactionsByPage(externalAccountId,
                page));
    }

    @Entao("valido que retornou registros da pagina {string}")
    public void validoQueRetornouRegistrosDaPagina(String page) {
        Assert.assertEquals(RequestManager.shared().getResponse().jsonPath().getString("number"), page);
    }
}
