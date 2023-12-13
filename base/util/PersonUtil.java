package base.util;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.*;

public class PersonUtil {
    public static final String MALE = "M";
    public static final String FEMALE = "F";
    private String name;
    private int age;
    private String cpf;
    private String rg;
    private String birthDate;
    private String gender;
    private String mother;
    private String father;
    private String email;
    private String phone;
    private String cellPhone;
    private Map<String, String> address;
    private int idOccupationConductor;
    private int idOccupationDock;
    private int rangePropertyValue;

    public PersonUtil() {
        setTheData();
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getCpf() { return cpf; }

    public String getRg() {
        return rg;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getGender() {
        return gender;
    }

    public String getMother() {
        return mother;
    }

    public String getFather() {
        return father;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getCellPhone() {
        return cellPhone;
    }

    public Map<String, String> getAddress() {
        return address;
    }

    public int getIdOccupationConductor() {
        return idOccupationConductor;
    }

    public int getIdOccupationDock() {
        return idOccupationDock;
    }

    public int getRangePropertyValue() {
        return rangePropertyValue;
    }

    public void setTheData() {
        String familyName = ' ' + generatesLastName() + ' ' + generatesLastName();

        this.gender = generateGender();
        setName(familyName);
        this.mother = generatesFemaleName() + familyName;
        this.father = generatesMaleName() + familyName;
        this.email = getName().toLowerCase().replaceAll(" ", ".") + '_'
                + MathUtil.getRandomNumber(MathUtil.ONE_HUNDRED_THOUSAND) + "@gmail.com";
        this.age = MathUtil.minAndMaxRandomNumber(MathUtil.EIGHTEEN, MathUtil.SEVENTY);
        this.cpf = new DocumentUtil().getRandomValidCPF(false);
        this.rg = new DocumentUtil().rg(false);
        this.birthDate = new DateTimeUtil().randomBirthday(
                MathUtil.ONE_THOUSAND_NINE_HUNDRED_SIXTY, MathUtil.TWO_THOUSAND);
        this.phone = String.valueOf(MathUtil.minAndMaxRandomNumber(MathUtil.TEN_MILLION, MathUtil.NINETY_NINE_MILLION));
        this.cellPhone = "99" + RandomStringUtils.randomNumeric(7);
        this.setRangePropertyValue(generateRangePropertyValue());
        generateOccupation();
        generateStreet();
    }

    public void setName(String familyName) {
        String personName;

        if (getGender().equals(MALE)) {
            personName = generatesMaleName();
        } else {
            personName = generatesFemaleName();
        }

        this.name = personName + familyName;
    }

    public void setCellPhone(String newCellPhone) {
        this.cellPhone = newCellPhone;
    }

    public void setEmail(String newEmail) {
        this.email = newEmail;
    }

    public void setRangePropertyValue(int newRangePropertyValue) {
        this.rangePropertyValue = newRangePropertyValue;
    }

    public void setIdOccupationByIdConductor(int newIdOccupationConductor) {
        updatePersonOccupation(newIdOccupationConductor, "CONDUCTOR");
    }

    public void setIdOccupationByIdDock(int newIdOccupationDock) {
        updatePersonOccupation(newIdOccupationDock, "DOCK");
    }

    public void setAddress(Map<String, String> newAddress) {
        this.address = newAddress;
    }

    private void generateOccupation() {
        String[] occupations = {"NOME = 'ASSALARIADO', CONDUCTOR: 1, DOCK: 2",
                "NOME: 'AUTONOMO', CONDUCTOR: 3, DOCK: 4",
                "NOME: 'PROF. LIBERAL', CONDUCTOR: 4, DOCK: 3",
                "NOME: 'OUTROS', CONDUCTOR: 6, DOCK: 5",
                "NOME: 'APOSENTADO', CONDUCTOR: 7, DOCK: 1",
                "NOME: 'VAZIO', CONDUCTOR: 0, DOCK: 0"};

        List<String> extractedOccupation = Arrays.asList(occupations[MathUtil.getRandomNumber(occupations.length - MathUtil.ONE)].split(","));

        this.idOccupationConductor = Integer.parseInt(extractedOccupation.get(1).trim().replaceAll("\\D+", ""));
        this.idOccupationDock = Integer.parseInt(extractedOccupation.get(2).trim().replaceAll("\\D+", ""));
    }

    private void updatePersonOccupation(int id, String base) {
        String[] occupations = {"NOME = 'ASSALARIADO', CONDUCTOR: 1, DOCK: 2",
                "NOME: 'AUTONOMO', CONDUCTOR: 3, DOCK: 4",
                "NOME: 'PROF. LIBERAL', CONDUCTOR: 4, DOCK: 3",
                "NOME: 'OUTROS', CONDUCTOR: 6, DOCK: 5",
                "NOME: 'APOSENTADO', CONDUCTOR: 7, DOCK: 1",
                "NOME: 'VAZIO', CONDUCTOR: 0, DOCK: 0"};

        for (String a : occupations) {
            if (a.contains(base.toUpperCase() + ": " + id)) {
                this.idOccupationConductor = Integer.parseInt(a.split(",")[1].trim().replaceAll("\\D+", ""));
                this.idOccupationDock = Integer.parseInt(a.split(",")[2].trim().replaceAll("\\D+", ""));
            }
        }
    }

    private int generateRangePropertyValue() {
        String[] ranges = {"5000", "55500", "300000", "750000", "1000001"};

        return Integer.parseInt(ranges[MathUtil.getRandomNumber(ranges.length)]);
    }

    public String generateGender() {
        String[] genderArr = {MALE, FEMALE};

        return genderArr[MathUtil.getRandomNumber(genderArr.length)];
    }

    public String generatesMaleName() {
        String[] maleName = {"Miguel", "Arthur", "Heitor", "Bernardo", "Théo", "Davi", "Gabriel", "Pedro", "Samuel",
                             "Lorenzo", "Benjamin", "Matheus", "Lucas", "Benício", "Gael", "Joaquim", "Nicolas",
                             "Henrique", "Rafael", "Isaac"};

        return maleName[MathUtil.getRandomNumber(maleName.length - MathUtil.ONE)];
    }

    public String generatesFemaleName() {
        String[] femaleName = {"Helena", "Alice", "Laura", "Manuela", "Isabella", "Sophia", "Luísa", "Heloísa",
                               "Valentina", "Júlia", "Cecília", "Eloá", "Lívia", "Lorena", "Maria Luísa",
                               "Giovanna", "Liz", "Antonella", "Maitê", "Mariana"};

        return femaleName[MathUtil.getRandomNumber(femaleName.length - MathUtil.ONE)];
    }

    public String generatesLastName() {
        String[] lastName = {"Agostinho", "Aguiar", "Albuquerque", "Alencastro", "Almeida", "Alves", "Alvim",
                             "Amorim", "Andrade", "Antunes", "Aragao", "Araujo", "Arruda", "Assis", "Assuncao",
                             "Avila", "Baptista", "Barbosa", "Barreto", "Barros", "Belchior", "Bernardes",
                             "Bittencourt", "Boaventura", "Bonfim", "Brites", "Brito", "Caetano", "Caldeira",
                             "Calixto", "Camacho", "Campos", "Capelo", "Cardoso", "Carvalho", "Castro", "Cavalcante",
                             "Cavalcanti", "Chaves", "Conceicao", "Cortes", "Costa", "Coutinho", "Cunha",
                             "da Conceicao", "da Costa", "da Cruz", "da Cunha", "da Luz", "da Mata",
                             "da Mota", "da Paz", "da Rocha", "da Rosa", "Damasio", "Dantas", "das Neves", "Dias",
                             "Dinis", "Domingues", "Dorneles", "dos Reis", "Drumond", "Duarte", "Escobar", "Espinosa",
                             "Esteves", "Evangelista", "Farias", "Felix", "Fernandes", "Ferrari", "Ferreira",
                             "Figueiredo", "Figueiroa", "Flores", "Fogaça", "Freitas", "Frutuoso", "Furtado",
                             "Galvao", "Garcia", "Gaspar", "Gentil", "Geraldes", "Gil", "Gomes", "Gonçalves",
                             "Gonzaga", "Goulart", "Gouveia", "Guedes", "Guimaraes", "Guterres", "Hernandes",
                             "Hilario", "Hipolito", "Ibrahim", "Ilha", "Infante", "Jaques", "Lacerda", "Leiria",
                             "Lessa", "Lima", "Lopes", "Machado", "Maciel", "Magalhaes", "Maia", "Maldonado",
                             "Marinho", "Marques", "Martins", "Medeiros", "Meireles", "Mello", "Melo", "Mendes",
                             "Menezes", "Mesquita", "Modesto", "Monteiro", "Moraes", "Morais", "Moreira",
                             "Morgado", "Moura", "Muniz", "Nascimento", "Neves", "Nobrega", "Nogueira", "Novaes",
                             "Novais", "Nunes", "Oliveira", "Ornelas", "Ourique", "Pacheco", "Padilha", "Paiva",
                             "Peixoto", "Peralta", "Pereira", "Peres", "Pilar", "Pimenta", "Pinheiro", "Pinto",
                             "Pires", "Portela", "Porto", "Quaresma", "Queiroz", "Ramires", "Ramos", "Rebelo",
                             "Resende", "Rezende", "Ribeiro", "Rocha", "Rodrigues", "Salazar", "Sales",
                             "Salgado", "Salgueiro", "Sampaio", "Sanches", "Santana", "Santos", "Silva", "Silveira",
                             "Siqueira", "Soares", "Souza", "Tavares", "Taveira", "Teixeira", "Teles", "Torres",
                             "Trindade", "Varela", "Vargas", "Vasconcelos", "Vasques", "Veiga", "Veloso", "Viana",
                             "Vidal", "Vieira", "Vilela", "Xavier", "Ximenes", "Xisco", "Zagalo", "Zaganelli",
                             "Zanette"};

        return lastName[MathUtil.getRandomNumber(lastName.length - MathUtil.ONE)];
    }

    public void generateStreet() {
        String[] allAddress = {"03978280, Rua das Savelhas,  , Conjunto Residencial Sitio Oratório, São Paulo, SP",
                "01134080, Praça Professor Antônio Peres Rodrigues Filho,  , Bom Retiro, São Paulo, SP",
                "05059020, Praça Silvestre Rabelo,  , Alto da Lapa, São Paulo, SP",
                "02376040, Rua Vilarinho,  , Tremembé, São Paulo, SP",
                "04814530, Rua Miguel Pereira dos Santos,  , Jardim Guanhembu, São Paulo, SP",
                "03256040, Rua Antônio Alves Santos,  , Vila Cunha Bueno, São Paulo, SP",
                "03244030, Rua Erva Imperial,  , Jardim Guairaca, São Paulo, SP",
                "05602090, Praça Carlos Amarante,  , Jardim Everest, São Paulo, SP",
                "02322200, Rua Alberto Dandy,  , Parque Casa de Pedra, São Paulo, SP",
                "02418110, Rua Coronel Antônio Agostinho Bezerra,  , Parque Mandaqui, São Paulo, SP",
                "02834100, Rua Capitão José Pais,  , Vila Nina, São Paulo, SP",
                "04121060, Rua Altino Rosa de Moraes,  , Vila Mariana, São Paulo, SP",
                "04462020, Rua Martinho Afonso,  , Jardim da Pedreira, São Paulo, SP",
                "08473543, Travessa Gaetano Andreozzi,  , Conjunto Habitacional Barro Branco II, São Paulo, SP",
                "03544120, Rua Lavras,  , Cidade Patriarca, São Paulo, SP",
                "88338610, Avenida dos Tucanos,  , Ariribá, Balneário Camboriú, SC",
                "88330643, Rua Alvin Bauer,  , Centro, Balneário Camboriú, SC",
                "71882015, Quadra QC 1 Conjunto 5,  , Riacho Fundo II, Brasília, DF",
                "69315656, Rua Laura Correa Moreira,  , São Bento, Boa Vista, RR",
                "76907762, Rua Mato Grosso,  , Dom Bosco, Ji-Paraná, RO",
                "58046085, Rua Rui Costa,  , Altiplano Cabo Branco, João Pessoa, PB",
                "66110210, Passagem Santo Amaro,  , Maracangalha, Belém, PA",
                "40713470, Travessa Dom Veloso,  , Itacaranha, Salvador, BA",
                "69911458, Rua Ana Nery,  , João Eduardo I, Rio Branco, AC",
                "49095789, Rua E,  , Jabotiana, Aracaju, SE",
                "94820310, Rua Vítor Hugo,  , Americana, Alvorada, RS",
                "26483080, Rua Amarante,  , Jardim Aljezur, Japeri, RJ"
        };

        List<String> extractedAddress = Arrays.asList(allAddress[MathUtil.getRandomNumber(allAddress.length - MathUtil.ONE)].split(","));

        this.address = new HashMap<>();
        this.address.put("zipCode", extractedAddress.get(0).trim());
        this.address.put("street", extractedAddress.get(1).trim());
        this.address.put("number", RandomStringUtils.randomNumeric(2).replaceFirst("0", ""));
        this.address.put("complement", extractedAddress.get(2).trim());
        this.address.put("neighborhood", extractedAddress.get(3).trim());
        this.address.put("city", extractedAddress.get(4).trim());
        this.address.put("state", extractedAddress.get(5).trim());
    }
}
