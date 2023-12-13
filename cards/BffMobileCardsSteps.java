package steps.cards;

import base.RequestManager;
import base.util.PropertiesUtil;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import org.junit.Assert;
import service.OrbiBffMobileCardService;
import service.OrbiIdentityManagerService;
import java.io.IOException;

public class BffMobileCardsSteps {

    private static final String FIRST_REGISTER_RESPONSE_BODY = "content.id[0]";
    OrbiBffMobileCardService orbiBffCardService;
    protected PropertiesUtil propertiesUtil;

    public BffMobileCardsSteps() throws IOException {
        orbiBffCardService = new OrbiBffMobileCardService();
        propertiesUtil = new PropertiesUtil();
    }

    @Quando("executo a requisicao para consultar as informacoes da home de cartoes")
    public void executoARequisicaoParaConsultarAsInformacoesDaHomeDeCartoes() {
        RequestManager.shared().setResponse(orbiBffCardService.getCardsHome());
    }

    @Quando("executo a requisicao para consultar um boleto informando o id {string}")
    public void executoARequisicaoParaConsultarBoletoPorId(String id) {
        RequestManager.shared().setResponse(orbiBffCardService.getBilletById(id));
    }

    @Dado("executo a requisicao para gerar o pdf do boleto informando o id {string}")
    public void executoARequisicaoParaGerarPdfDoBoletoPorId(String id) {
        RequestManager.shared().setResponse(orbiBffCardService.getBilletPdfById(id));
    }

    @Entao("valido que o campo {string} retornou contendo as transacoes da conta")
    public void validoQueOCampoRetornouContendoAsTransacoesDaConta(String transacoesRecentes) {
        Assert.assertFalse(RequestManager.shared().getResponse().getBody()
                .jsonPath().getList(transacoesRecentes).isEmpty());
    }

    @Quando("executo a requisicao para consultar os cartoes dessa conta a partir do bff")
    public void executoARequisicaoParaConsultarOsCartoesDessaContaAPartirDoBff() {
        RequestManager.shared().setResponse(orbiBffCardService.getCardsFromBff());
    }

    @Entao("valido que o {string} retornou no BFF")
    public void validoQueORetornouNoBFF(String descricaoCartao) {
        Assert.assertEquals(RequestManager.shared().getResponse().jsonPath()
                .getString("content.description"), descricaoCartao);
    }

    @Entao("valido que retornou o {string} no campo {string}")
    public void validoQueRetornouONoCampo(String descricao, String campo) {
        Assert.assertEquals(RequestManager.shared().getResponse().jsonPath()
                .getString(campo), descricao);
    }

    @Quando("a partir do BFF executo a requisicao para consultar os dados reais do cartao virtual da conta")
    public void aPartirDoBFFExecutoARequisicaoParaConsultarOsDadosReaisDoCartaoVirtualDaConta() {
        RequestManager.shared().setResponse(orbiBffCardService.getCardId());
        String cardId = RequestManager.shared().getResponse().jsonPath().getString(FIRST_REGISTER_RESPONSE_BODY);
        RequestManager.shared().setResponse(orbiBffCardService.getCardRealData(cardId));
    }

    @Quando("executo a requisicao para atualizar o status do cartao virtual da conta para o status {string} no BFF")
    public void executoARequisicaoParaAtualizarOStatusDoCartaoVirtuakDaContaParaOStatusNoBFF(String cardStatus) {
        RequestManager.shared().setResponse(orbiBffCardService.getCardId());
        String cardId = RequestManager.shared().getResponse().jsonPath().getString(FIRST_REGISTER_RESPONSE_BODY);
        RequestManager.shared().setResponse(orbiBffCardService.updateCardStatus(cardId, cardStatus));
    }

    @Quando("executo a requisicao para atualizar o status do cartao {string} para o status {string} no BFF")
    public void executoARequisicaoParaAtualizarOStatusDoCartaoParaOStatusNoBFF(String cardUID, String cardStatus) {
        RequestManager.shared().setResponse(orbiBffCardService.updateCardStatus(cardUID, cardStatus));
    }

    @Quando("a partir do BFF executo a requisicao para criar o cartao {string} com a flag isBlocked {string}")
    public void aPartirDoBFFExecutoARequisicaoParaCriarOCartaoComAFlagIsBlocked(String cardTpe, String isBlocked) {
        RequestManager.shared().setResponse(orbiBffCardService.createCard(cardTpe, isBlocked));
    }

    @Quando("a partir do BFF executo a requisicao para atualizar o cvv dinamico do cartao virtual da conta")
    public void aPartirDoBFFExecutoARequisicaoParaAtualizarOCvvDinamicoDoCartaoVirtualDaConta() {
        RequestManager.shared().setResponse(orbiBffCardService.getCardId());
        String cardId = RequestManager.shared().getResponse().jsonPath().getString(FIRST_REGISTER_RESPONSE_BODY);
        RequestManager.shared().setResponse(orbiBffCardService.updateDynamicCvv(cardId));
    }

    @Quando("a partir do BFF executo a requisicao para consultar o limite da conta valida")
    public void aPartirDoBFFExecutoARequisicaoParaConsultarOLimiteDaConta() {
        RequestManager.shared().setResponse(orbiBffCardService.getAccountLimit());
    }

    @Quando("que estou logado no orbi com novo usuario")
    public void queEstouLogadoNoOrbiComNovoUsuario() throws IOException {
        String deviceId = propertiesUtil.getPropertyByName("bff_deviceid");
        String cpf = RequestManager.shared().getHelp();
        String password = propertiesUtil.getPropertyByName("bff_password_with_virtual_credit");
        RequestManager.shared().setRequest(new OrbiIdentityManagerService().authenticate(deviceId, cpf, password));
    }

    @Quando("a partir do BFF com novo cpf executo a requisicao para criar o cartao {string} com a flag isBlocked {string}")
    public void aPartirDoBFFComNovoCpfExecutoARequisicaoCriarCartaoComFlagIsBlocked(String cardTpe, String isBlocked) {
        RequestManager.shared().setResponse(orbiBffCardService.createCard(cardTpe, isBlocked));
    }

    @Quando("executo a requisicao para atualizar o status do cartao virtual de credito da conta para o status {string} no BFF")
    public void executoRequisicaoParaAtualizarStatusDoCartaoVirtualDeCreditoDaContaParaOStatusNoBFF(String cardStatus) {
        RequestManager.shared().setResponse(orbiBffCardService.getVirtualCreditCard());
        String cardId = RequestManager.shared().getResponse().jsonPath().getString(FIRST_REGISTER_RESPONSE_BODY);
        RequestManager.shared().setResponse(orbiBffCardService.updateCardStatus(cardId, cardStatus));
    }

}
