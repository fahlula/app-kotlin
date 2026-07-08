package org.app

class App {
    val idadeMinima = 18

    val ofertas = mutableListOf<Oferta>()
    val pessoas = mutableListOf<Pessoa>()
    val contratacoes = mutableListOf<Contratacao>()
    val telas = Telas()

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
            val contratacaoRealizada = cadastrarContratacao()

            if (contratacaoRealizada) {
                continuar = false
            }
        }

        telas.mostrarRelatorioGeral(pessoas, contratacoes)
    }

    fun cadastrarContratacao(): Boolean {
        telas.mostrarTituloCadastro()

        telas.pedirNome()
        val nome = readln()

        telas.pedirIdade()
        val idade = readln().toInt()

        val contratante = Pessoa(nome, idade)

        if (!contratante.ehMaiorDeIdade(idadeMinima)) {
            pessoas.add(contratante)
            telas.mostrarMensagemMenorDeIdade()
            return false
        }

        var continuarContratando = true

        while (continuarContratando) {

            val respostas = mutableListOf<RespostaOferta>()

            val opcaoAssinatura = perguntarAssinaturaParaQuem()
            val usuario: Pessoa

            if (opcaoAssinatura == 1) {
                usuario = contratante
            } else {
                telas.mostrarTituloCadastroDependente()
                telas.pedirNomeDependente()
                val nomeDependente = readln()

                telas.pedirIdadeDependente()
                val idadeDependente = readln().toInt()

                usuario = Pessoa(nomeDependente, idadeDependente)
            }

            val ofertasPermitidas = mutableListOf<Oferta>()

            for (oferta in ofertas) {
                if (usuario.idade >= oferta.idadeMinima) {
                    ofertasPermitidas.add(oferta)
                }
            }


            telas.mostrarOfertas(usuario, ofertasPermitidas)

            telas.pedirEscolhasOfertas()
            val escolhasDigitadas = readln()

            val partes = escolhasDigitadas.split(",")
            val numerosEscolhidos = mutableListOf<Int>()

            for (parte in partes) {
                val numero = parte.trim().toInt()

                if (numero !in numerosEscolhidos && ofertasPermitidas.any {
                        it.numero == numero
                    }) {
                    numerosEscolhidos.add(numero)
                }
            }

            for (oferta in ofertasPermitidas) {
                val aceitou = oferta.numero in numerosEscolhidos
                val resposta = RespostaOferta(oferta, aceitou)
                respostas.add(resposta)
            }

            val contratacao = Contratacao(contratante, usuario, respostas)
            contratacoes.add(contratacao)

            continuarContratando = perguntarSeDesejaContratarOutraPessoa()
        }


        return true
    }

    fun perguntarSeDesejaContratarOutraPessoa(): Boolean {
        var opcao: Int

        do {
            telas.mostrarPerguntaContratarOutraPessoa()

            opcao = readln().toInt()

            if (opcao != 1 && opcao != 2) {
                telas.mostrarOpcaoInvalida()
            }

        } while (opcao != 1 && opcao != 2)

        return opcao == 1
    }

    fun perguntarAssinaturaParaQuem(): Int {
        var opcao: Int

        do {
            telas.mostrarPerguntaAssinaturaParaQuem()

            opcao = readln().toInt()

            if (opcao != 1 && opcao != 2) {
                telas.mostrarOpcaoInvalida()
            }

        } while (opcao != 1 && opcao != 2)

        return opcao
    }
}

class Pessoa(
    val nome: String,
    val idade: Int
) {
    fun ehMaiorDeIdade(idadeMinima: Int): Boolean {
        return idade >= idadeMinima
    }
}

class Contratacao(
    val contratante: Pessoa,
    val usuario: Pessoa,
    val respostas: MutableList<RespostaOferta>
)

class Oferta(
    val numero: Int,
    val nome: String,
    val idadeMinima: Int

)

class RespostaOferta(
    val oferta: Oferta,
    val aceitou: Boolean
)

class Telas {
    fun mostrarTituloCadastro() {
        println()
        println("=== CADASTRO ===")
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
        println()
        println("Ofertas disponíveis para ${usuario.nome}:")

        for (oferta in ofertas) {
            println("${oferta.numero} - ${oferta.nome}")
        }
    }

    fun pedirEscolhasOfertas() {
        println()
        println("Digite os números das ofertas que deseja contratar separados por vírgula:")
        println("Exemplo: 1,5,6 (Digite sem dar espaços entre as vírgulas)")
        print("Escolhas: ")
    }

    fun mostrarPerguntaContratarOutraPessoa() {
        println()
        println("Gostaria de contratar outra assinatura para outra pessoa?")
        println("1 - Sim")
        println("2 - Não")
        print("Opção: ")
    }

    fun mostrarOpcaoInvalida() {
        println()
        println("Opção inválida, tente novamente!")
    }

    fun mostrarPerguntaAssinaturaParaQuem() {
        println()
        println("A assinatura será para quem?")
        println("1 - Para mim")
        println("2 - Para um dependente")
        print("Opção: ")
    }

    fun mostrarTituloCadastroDependente() {
        println()
        println("=== CADASTRO DO DEPENDENTE ===")
    }

    fun pedirNomeDependente() {
        print("Digite o nome do dependente: ")
    }

    fun pedirIdadeDependente() {
        print("Digite a idade do dependente: ")
    }

    fun mostrarRelatorioGeral(
        pessoas: MutableList<Pessoa>,
        contratacoes: MutableList<Contratacao>
    ) {
        println()
        println("=== RELATÓRIO GERAL ===")

        println()
        println("=== PESSOAS QUE NÃO CONSEGUIRAM CONTRATAR ===")

        for (pessoa in pessoas) {
            println()
            println("Nome: ${pessoa.nome}")
            println("Idade: ${pessoa.idade}")
            println("Não realizou contratação.")
            println("Motivo: menor de idade não pode contratar serviços.")
        }

        println()
        println("=== CONTRATAÇÕES REALIZADAS ===")

        for (contratacao in contratacoes) {
            println()
            println("Contratante: ${contratacao.contratante.nome}")

            if (contratacao.usuario.nome == contratacao.contratante.nome &&
                contratacao.usuario.idade == contratacao.contratante.idade
            ) {
                println("Assinatura para o próprio contratante")
                println("Idade do contratante: ${contratacao.contratante.idade}")
            } else {
                println("Assinatura para dependente")
                println("Dependente: ${contratacao.usuario.nome}")
                println("Idade do dependente: ${contratacao.usuario.idade}")
            }

            println("Ofertas:")

            for (resposta in contratacao.respostas) {
                if (resposta.aceitou) {
                    println("${resposta.oferta.numero} - ${resposta.oferta.nome} - ACEITA")
                } else {
                    println("${resposta.oferta.numero} - ${resposta.oferta.nome} - RECUSADA")
                }
            }
        }
    }
}

fun main() {
    val app = App()
    app.rodar()
}