package org.app

class App {
    val idadeMinima = 18

    val ofertas = mutableListOf<Oferta>()
    val pessoas = mutableListOf<Pessoa>()
    val contratacoes = mutableListOf<Contratacao>()
    val contratacoesFamilia = mutableListOf<ContratacaoFamilia>()
    val telas = Telas()
    var proximoIdPessoa = 1

    init {
        ofertas.add(Oferta(1, "Filmes", 14)) // 14
        ofertas.add(Oferta(2, "Séries", 16)) // 16
        ofertas.add(Oferta(3, "Documentários", 0))
        ofertas.add(Oferta(4, "Esportes", 0))
        ofertas.add(Oferta(5, "Futebol", 0))
        ofertas.add(Oferta(6, "MMA", 18)) // 18
        ofertas.add(Oferta(7, "Jogos", 16)) // 16
        ofertas.add(Oferta(8, "Música", 0))
        ofertas.add(Oferta(9, "Notícias", 0))
        ofertas.add(Oferta(10, "Cursos", 0))
    }

    fun rodar() {
        var continuar = true

        while (continuar) {
            val fezContratacao = cadastrarContratacao()
            if (fezContratacao) {
                continuar = perguntarSeDesejaFazerOutraContratacao()
            }
        }

        telas.mostrarRelatorioGeral(pessoas, contratacoes, contratacoesFamilia)
    }

    fun criarPessoa(nome: String, idade: Int): Pessoa {
        val pessoa = Pessoa(proximoIdPessoa, nome, idade)
        proximoIdPessoa = proximoIdPessoa + 1
        return pessoa
    }

    fun cadastrarContratacao(): Boolean {
        telas.mostrarTituloCadastro()

        telas.pedirNome()
        val nome = lerNome()

        telas.pedirIdade()
        val idade = lerIdade()

        val contratante = criarPessoa(nome, idade)

        if (!contratante.ehMaiorDeIdade(idadeMinima)) {
            pessoas.add(contratante)
            telas.mostrarMensagemMenorDeIdade()
            return false
        }

        val tipo = perguntarTipoAssinatura()

        if (tipo == 1) {
            contratarIndividual(contratante)
        } else {
            contratarFamilia(contratante)
        }

        return true
    }

    fun contratarIndividual(contratante: Pessoa) {
        val respostas = escolherOfertas(contratante)

        val contratacao = Contratacao(contratante, respostas)
        contratacoes.add(contratacao)
    }

    fun contratarFamilia(contratante: Pessoa) {
        telas.mostrarTituloAssinaturaFamilia()

        val contratacaoFamilia = ContratacaoFamilia(contratante)

        val respostasContratante = escolherOfertas(contratante)
        val membroContratante = MembroFamilia(contratante, respostasContratante)
        contratacaoFamilia.adicionarMembro(membroContratante)

        var continuarAdicionando = true

        while (continuarAdicionando) {
            val dependente = cadastrarDependente()

            val respostas = membroContratante.ofertasHerdadasPara(dependente)

            if (respostas.isEmpty()) {
                telas.mostrarDependenteSemOfertas()
                continuarAdicionando = perguntarSeDesejaAdicionarDependente()
                continue
            }

            telas.mostrarOfertasHerdadas(dependente, respostas)
            contratacaoFamilia.adicionarMembro(MembroFamilia(dependente, respostas))

            continuarAdicionando = perguntarSeDesejaAdicionarDependente()
        }

        contratacoesFamilia.add(contratacaoFamilia)
    }

    fun cadastrarDependente(): Pessoa {
        telas.mostrarTituloCadastroDependente()

        telas.pedirNomeDependente()
        val nomeDependente = lerNome()

        telas.pedirIdadeDependente()
        val idadeDependente = lerIdade()

        return criarPessoa(nomeDependente, idadeDependente)
    }

    fun escolherOfertas(usuario: Pessoa): MutableList<RespostaOferta> {
        val respostas = mutableListOf<RespostaOferta>()

        val ofertasPermitidas = mutableListOf<Oferta>()

        for (oferta in ofertas) {
            if (oferta.permitidaPara(usuario)) {
                ofertasPermitidas.add(oferta)
            }
        }

        telas.mostrarOfertas(usuario, ofertasPermitidas)

        telas.pedirEscolhasOfertas()
        var escolhasValidas = false
        val numerosEscolhidos = mutableListOf<Int>()

        while (!escolhasValidas) {
            val escolhasDigitadas = readln().trim()
            numerosEscolhidos.clear()

            if (escolhasDigitadas.isEmpty()) {
                escolhasValidas = true
                continue
            }

            val partes = escolhasDigitadas.split(",")
            var temInvalido = false

            for (parte in partes) {
                val numero = parte.trim().toIntOrNull()

                if (numero == null || ofertasPermitidas.none { it.numero == numero }) {
                    temInvalido = true
                } else if (numero !in numerosEscolhidos) {
                    numerosEscolhidos.add(numero)
                }
            }

            if (temInvalido) {
                telas.mostrarOpcaoInvalida()
                telas.pedirEscolhasOfertas()
            } else {
                escolhasValidas = true
            }
        }

        for (oferta in ofertasPermitidas) {
            val aceitou = oferta.numero in numerosEscolhidos
            val resposta = RespostaOferta(oferta, aceitou)
            respostas.add(resposta)
        }

        return respostas
    }

    fun lerInteiro(): Int {
        var valor = readln().toIntOrNull()

        while (valor == null) {
            telas.mostrarEntradaInvalida()
            valor = readln().toIntOrNull()
        }

        return valor
    }

    fun lerNome(): String {
        var nome = readln().trim()

        while (nome.isEmpty()) {
            telas.mostrarNomeInvalido()
            nome = readln().trim()
        }

        return nome
    }

    fun lerIdade(): Int {
        var idade = lerInteiro()

        while (idade !in 0..120) {
            telas.mostrarIdadeInvalida()
            idade = lerInteiro()
        }

        return idade
    }

    fun perguntarSeDesejaFazerOutraContratacao(): Boolean {
        var opcao: Int

        do {
            telas.mostrarPerguntaFazerOutraContratacao()

            opcao = lerInteiro()

            if (opcao != 1 && opcao != 2) {
                telas.mostrarOpcaoInvalida()
            }

        } while (opcao != 1 && opcao != 2)

        return opcao == 1
    }

    fun perguntarSeDesejaAdicionarDependente(): Boolean {
        var opcao: Int

        do {
            telas.mostrarPerguntaAdicionarDependente()

            opcao = lerInteiro()

            if (opcao != 1 && opcao != 2) {
                telas.mostrarOpcaoInvalida()
            }

        } while (opcao != 1 && opcao != 2)

        return opcao == 1
    }

    fun perguntarTipoAssinatura(): Int {
        var opcao: Int

        do {
            telas.mostrarPerguntaTipoAssinatura()

            opcao = lerInteiro()

            if (opcao != 1 && opcao != 2) {
                telas.mostrarOpcaoInvalida()
            }

        } while (opcao != 1 && opcao != 2)
        return opcao
    }

}

class MembroFamilia(
    val pessoa: Pessoa,
    val respostas: MutableList<RespostaOferta>
) {
    fun ofertasHerdadasPara(dependente: Pessoa): MutableList<RespostaOferta> {
        val herdadas = mutableListOf<RespostaOferta>()

        for (resposta in respostas) {
            if (resposta.aceitou && resposta.oferta.permitidaPara(dependente)) {
                herdadas.add(RespostaOferta(resposta.oferta, true))
            }
        }

        return herdadas
    }
}

class ContratacaoFamilia(
    val contratante: Pessoa
) {
    private val _membros = mutableListOf<MembroFamilia>()
    val membros: List<MembroFamilia>
        get() = _membros

    fun adicionarMembro(membro: MembroFamilia) {
        _membros.add(membro)
    }
}


class Pessoa(
    val id: Int,
    val nome: String,
    val idade: Int
) {
    fun ehMaiorDeIdade(idadeMinima: Int): Boolean {
        return idade >= idadeMinima
    }
}

class Contratacao(
    val contratante: Pessoa,
    val respostas: MutableList<RespostaOferta>
)

class Oferta(
    val numero: Int,
    val nome: String,
    val idadeMinima: Int
) {
    fun permitidaPara(pessoa: Pessoa): Boolean {
        return pessoa.idade >= idadeMinima
    }

    fun descricao(): String {
        return "$numero - $nome"
    }
}

class RespostaOferta(
    val oferta: Oferta,
    val aceitou: Boolean
) {
    fun descricao(): String {
        val status = if (aceitou) "ACEITA" else "RECUSADA"
        return "${oferta.descricao()} - $status"
    }
}

class Telas {
    fun mostrarTituloCadastro() {
        println("\n=== CADASTRO ===")
    }

    fun pedirNome() {
        print("Digite seu nome: ")
    }

    fun pedirIdade() {
        print("Digite sua idade: ")
    }

    fun mostrarMensagemMenorDeIdade() {
        println("Menor de idade não pode contratar serviços.")
    }

    fun mostrarOfertas(usuario: Pessoa, ofertas: MutableList<Oferta>) {
        println("\nOfertas disponíveis para ${usuario.nome}:")

        for (oferta in ofertas) {
            println(oferta.descricao())
        }
    }

    fun mostrarDependenteSemOfertas() {
        println("\nNão há ofertas disponíveis para este dependente neste plano.")
    }

    fun mostrarOfertasHerdadas(dependente: Pessoa, respostas: MutableList<RespostaOferta>) {
        println("\nOfertas herdadas por ${dependente.nome}:")

        for (resposta in respostas) {
            println(resposta.oferta.descricao())
        }
    }

    fun pedirEscolhasOfertas() {
        println("\nDigite os números das ofertas que deseja contratar separados por vírgula:\n" +
                "Exemplo: 1,5,6 (Digite sem dar espaços entre as vírgulas)\n" +
                "Ou apenas aperte Enter para não contratar nenhuma.")
        print("Escolhas: ")
    }

    fun mostrarPerguntaFazerOutraContratacao() {
        println("\nOutra pessoa deseja fazer uma contratação?\n" +
                "1 - Sim, cadastrar outra pessoa\n" +
                "2 - Não, encerrar")
        print("Opção: ")
    }

    fun mostrarOpcaoInvalida() {
        println("\nOpção inválida, tente novamente!")
    }

    fun mostrarEntradaInvalida() {
        println("\nEntrada inválida. Digite apenas números.")
        print("Digite novamente: ")
    }

    fun mostrarNomeInvalido() {
        println("\nNome inválido. Digite pelo menos um caractere.")
        print("Digite novamente: ")
    }

    fun mostrarIdadeInvalida() {
        println("\nIdade inválida. Digite um valor entre 0 e 120.")
        print("Digite novamente: ")
    }

    fun mostrarTituloCadastroDependente() {
        println("\n=== CADASTRO DO DEPENDENTE ===")
    }

    fun pedirNomeDependente() {
        print("Digite o nome do dependente: ")
    }

    fun pedirIdadeDependente() {
        print("Digite a idade do dependente: ")
    }

    fun mostrarPerguntaTipoAssinatura() {
        println("\nA assinatura será qual tipo?\n" +
                "1 - Individual\n" +
                "2 - Familia")
        print("Opção: ")
    }

    fun mostrarTituloAssinaturaFamilia() {
        println("\n=== ASSINATURA FAMÍLIA ===\n" +
                "Você escolhe as ofertas do plano e cada dependente herda o que for compatível com a idade dele.")
    }

    fun mostrarPerguntaAdicionarDependente() {
        println("\nGostaria de adicionar outro dependente na assinatura família?\n" +
                "1 - Sim\n" +
                "2 - Não")
        print("Opção: ")
    }

    fun mostrarRelatorioGeral(
        pessoas: MutableList<Pessoa>,
        contratacoes: MutableList<Contratacao>,
        contratacoesFamilia: MutableList<ContratacaoFamilia>
    ) {
        println("\n=== RELATÓRIO GERAL ===")

        println("\n=== PESSOAS QUE NÃO CONSEGUIRAM CONTRATAR ===")

        if (pessoas.isEmpty()) {
            println("Lista vazia.")
        } else {
            for (pessoa in pessoas) {
                println("\nNome: ${pessoa.nome}")
                println("Idade: ${pessoa.idade}")
                println("Não realizou contratação.")
                println("Motivo: menor de idade não pode contratar serviços.")
            }
        }

        println("\n=== ASSINATURAS INDIVIDUAIS ===")

        if (contratacoes.isEmpty()) {
            println("Lista vazia.")
        } else {
            for (contratacao in contratacoes) {
                println("\nContratante: ${contratacao.contratante.nome}\n" +
                        "Idade do contratante: ${contratacao.contratante.idade}\n" +
                        "Ofertas:")

                for (resposta in contratacao.respostas) {
                    println(resposta.descricao())
                }
            }
        }

        println("\n=== ASSINATURAS FAMÍLIA ===")

        if (contratacoesFamilia.isEmpty()) {
            println("Lista vazia.")
        } else {
            for (familia in contratacoesFamilia) {
                println("\nContratante: ${familia.contratante.nome}\n" +
                        "Membros da família: ${familia.membros.size}")

                for (membro in familia.membros) {
                    val ehContratante = membro.pessoa.id == familia.contratante.id

                    if (ehContratante) {
                        println("\nMembro (contratante): ${membro.pessoa.nome}")
                    } else {
                        println("\nMembro (dependente): ${membro.pessoa.nome}")
                    }

                    println("Idade: ${membro.pessoa.idade}\n" +
                            "Ofertas:")

                    for (resposta in membro.respostas) {
                        if (ehContratante) {
                            println(resposta.descricao())
                        } else {
                            println(resposta.oferta.descricao())
                        }
                    }
                }
            }
        }
    }
}

fun main() {
    val app = App()
    app.rodar()
}