package steps.cards;

import base.RequestManager;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import org.json.JSONObject;
import service.DockConnectorService;

import java.io.IOException;
import java.util.List;

public class DockConnectorCardsControllerSteps extends DockConnectorService {

    private static final String EXTERNAL_CARD_ID_FIELD_NAME = "externalCardId";
    private static final String ID_FIELD_NAME = "id";
    private static final String ACCOUNT_DOCK_ID = "accountDockId";
    private static final String DOCK_CARD_ID_PROPETIES_VALUE = "dock_card_id";
    private static final String DOCK_ACCOUNT_ID_PROPERTIES_VALUE = "account_dock_id";
    private static final String FLAG_ATIVADO = "ativado";

    DockConnectorService dockConnectorService;

    public DockConnectorCardsControllerSteps() throws IOException {
        dockConnectorService = new DockConnectorService();
    }

    @Quando("executo a requisicao para criar o cartao provisorio para a conta {string}")
    public void executoARequisicaoParaCriarOCartaoProvisorioParaAConta(String externalAccountId) {
        RequestManager.shared().setResponse(dockConnectorService.createProvisoryCard(externalAccountId));
    }

    @Quando("executo a requisicao para criar o cartao virtual na dock {string} para a conta valida")
    public void executoARequisicaoParaCriarOCartaoVirtualNaDockParaAConta(String isBlocked) {
        String externalAccountId = propertiesUtil.getPropertyByName(DOCK_ACCOUNT_ID_PROPERTIES_VALUE);
        RequestManager.shared().setResponse(dockConnectorService.createDockVirtualCard(isBlocked, externalAccountId));
    }

    @Quando("executo a requisicao para criar o cartao virtual na dock {string} para a conta inexistente")
    public void executoARequisicaoParaCriarOCartaoVirtualNaDockParaAContaInexistente(String isBlocked) {
        String externalAccountId = propertiesUtil.getPropertyByName("invalid_account_dock_id");
        RequestManager.shared().setResponse(dockConnectorService.createDockVirtualCard(isBlocked, externalAccountId));
    }

    @Quando("executo a requisicao para criar o cartao virtual na dock {string} para a conta {string}")
    public void executoARequisicaoParaCriarOCartaoVirtualNaDockParaAConta(String isBlocked, String externalAccountId) {
        RequestManager.shared().setResponse(dockConnectorService.createDockVirtualCard(isBlocked, externalAccountId));
    }

    @Quando("executo a requisicao para criar o cartao provisorio para a conta nova")
    public void executoARequisicaoParaCriarOCartaoProvisorioParaAContaNova() {
        String externalAccountId = RequestManager.shared().getResponse()
                .then().extract().path(ID_FIELD_NAME).toString();
        RequestManager.shared().setResponse(dockConnectorService.waitForCardsCreationprovisional(externalAccountId));
    }

    @Quando("executo a requisicao para criar o cartao virtual na dock {string} para a conta nova")
    public void executoARequisicaoParaCriarOCartaoVirtualNaDockParaAContaNova(String isBlocked) {
        String externalAccountId = RequestManager.shared().getResponse()
                .then().extract().path(ID_FIELD_NAME).toString();
        RequestManager.shared().setResponse(dockConnectorService.createDockVirtualCard(isBlocked, externalAccountId));
    }

    @Quando("executo a requisicao para criar o cartao virtual na dock sem informar tag de bloqueio para a conta nova")
    public void executoARequisicaoParaCriarOCartaoVirtualNaDockSemInformarTagDeBloqueioParaAContaNova() {
        String externalAccountId = RequestManager.shared().getResponse()
                .then().extract().path(ID_FIELD_NAME).toString();
        RequestManager.shared().setResponse(dockConnectorService.createDockVirtualCard(null, externalAccountId));
    }

    @Quando("executo a requisicao para consultar os cartoes da conta {string} informando a ordenacao {string}")
    public void executoARequisicaoParaConsultarOsCa(String externalAccountId, String ordenacao) {
        RequestManager.shared().setResponse(dockConnectorService
                .getCardsByExternalAccountId(externalAccountId, ordenacao));
    }

    @Entao("valido que a ordernacao {string} esta correta")
    public void validoQueAOrdernacaoEstaCorreta(String ordenacao) {
        List<Integer> listaIdCartao = RequestManager.shared().getResponse().getBody().jsonPath().getList("content.id");
        dockConnectorService.validateSortDirection(listaIdCartao, ordenacao);
    }

    @E("crio um cartao virtual para a conta criada na account")
    public void crioUmCartaoVirtualParaAContaCriadaNaAccount() {
        String accountDockId = new JSONObject(RequestManager.shared().getHelp()).get(ACCOUNT_DOCK_ID).toString();
        RequestManager.shared().setResponse(
                dockConnectorService.createDockVirtualCard(FLAG_ATIVADO, accountDockId));
    }

    @Quando("executo a requisicao para bloquear o cartao virtual existente")
    public void executoARequisicaoParaBloquearOCartaoVirtual() {
        String idCard = propertiesUtil.getPropertyByName(DOCK_CARD_ID_PROPETIES_VALUE);
        RequestManager.shared().setResponse(dockConnectorService.blockVirtualCard(idCard));
    }

    @Quando("executo a requisicao para bloquear o cartao virtual {string} invalido")
    @Quando("executo a requisicao para bloquear o cartao virtual {string} inexistente")
    public void executoARequisicaoParaBloquearOCartaoVirtualInvalido(String idCard) {
        RequestManager.shared().setResponse(dockConnectorService.blockVirtualCard(idCard));
    }

    @Quando("executo a requisicao para desbloquear o cartao virtual existente")
    public void executoARequisicaoParaDesbloquearOCartaoVirtual() {
        String idCard = propertiesUtil.getPropertyByName(DOCK_CARD_ID_PROPETIES_VALUE);
        RequestManager.shared().setResponse(dockConnectorService.unblockVirtualCard(idCard));
    }

    @E("crio um cartao virtual")
    public void crioUmCartaoVirtual() {
        String accountDockId = RequestManager.shared().getHelpJsonObject().get(ACCOUNT_DOCK_ID).toString();
        RequestManager.shared().setResponse(
                dockConnectorService.createDockVirtualCard(FLAG_ATIVADO, accountDockId));
    }

    @Quando("executo a requisicao para criar um cvv dinamico para o cartao virtual")
    public void executoARequisicaoParaCriarUmCvvDinamicoParaOCartaoVirtual() {
        String cardId = RequestManager.shared().getResponse()
                .getBody().jsonPath().getString(EXTERNAL_CARD_ID_FIELD_NAME);
        RequestManager.shared().setResponse(dockConnectorService.createDynamicCvv(cardId, "2022-09-13T16:36:00Z"));
    }

    @Dado("executo a requisicao para consular um cvv dinamico para o cartao virtual")
    public void executoARequisicaoParaConsularUmCvvDinamicoParaOCartaoVirtual() {
        String cardId = RequestManager.shared().getResponse()
                .getBody().jsonPath().getString(EXTERNAL_CARD_ID_FIELD_NAME);
        RequestManager.shared().setResponse(dockConnectorService.getCvvDynamic(cardId));
    }

    @Quando("executo a requisicao para deletar o cvv dinamico do cartao virtual")
    public void executoARequisicaoParaDeletarOCvvDinamicoDoCartaoVirtual() {
        String cardId = RequestManager.shared().getResponse()
                .getBody().jsonPath().getString(EXTERNAL_CARD_ID_FIELD_NAME);
        RequestManager.shared().setResponse(dockConnectorService.deleteCvvDynamic(cardId));
    }

}
