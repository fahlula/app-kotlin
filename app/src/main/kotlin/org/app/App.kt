package org.app

class App {
    val idadeMinima = 18

    val ofertas = mutableListOf<Oferta>()
    val pessoas = mutableListOf<Pessoa>()
    val contratacoes = mutableListOf<Contratacao>()
    val contratacoesFamilia = mutableListOf<ContratacaoFamilia>()
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
            val fezContratacao = cadastrarContratacao()
            if (fezContratacao) {
                continuar = perguntarSeDesejaFazerOutraContratacao()
            }
        }

        telas.mostrarRelatorioGeral(pessoas, contratacoes, contratacoesFamilia)
    }

    fun cadastrarContratacao(): Boolean {
        telas.mostrarTituloCadastro()

        telas.pedirNome()
        val nome = readln()

        telas.pedirIdade()
        val idade = lerInteiro()

        val contratante = Pessoa(nome, idade)

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

        val contratacao = Contratacao(contratante, contratante, respostas)
        contratacoes.add(contratacao)
    }

    fun contratarFamilia(contratante: Pessoa) {
        telas.mostrarTituloAssinaturaFamilia()

        val membros = mutableListOf<MembroFamilia>()

        val respostasContratante = escolherOfertas(contratante)
        membros.add(MembroFamilia(contratante, respostasContratante))

        var continuarAdicionando = true

        while (continuarAdicionando) {
            val dependente = cadastrarDependente()

            val respostas = escolherOfertas(dependente)
            membros.add(MembroFamilia(dependente, respostas))

            continuarAdicionando = perguntarSeDesejaAdicionarDependente()
        }

        val contratacaoFamilia = ContratacaoFamilia(contratante, membros)
        contratacoesFamilia.add(contratacaoFamilia)
    }

    fun cadastrarDependente(): Pessoa {
        telas.mostrarTituloCadastroDependente()

        telas.pedirNomeDependente()
        val nomeDependente = readln()

        telas.pedirIdadeDependente()
        val idadeDependente = lerInteiro()

        return Pessoa(nomeDependente, idadeDependente)
    }

    fun escolherOfertas(usuario: Pessoa): MutableList<RespostaOferta> {
        val respostas = mutableListOf<RespostaOferta>()

        val ofertasPermitidas = mutableListOf<Oferta>()

        for (oferta in ofertas) {
            if (usuario.idade >= oferta.idadeMinima) {
                ofertasPermitidas.add(oferta)
            }
        }

        telas.mostrarOfertas(usuario, ofertasPermitidas)

        telas.pedirEscolhasOfertas()
        var escolhasValidas = false
        val numerosEscolhidos = mutableListOf<Int>()

        while (!escolhasValidas) {
            val escolhasDigitadas = readln()

            val partes = escolhasDigitadas.split(",")
            numerosEscolhidos.clear()
            var temInvalido = false

            for (parte in partes) {
                val numero = parte.trim().toIntOrNull()

                if (numero != null && numero !in numerosEscolhidos && ofertasPermitidas.any { it.numero == numero }) {
                    numerosEscolhidos.add(numero)
                } else {
                    temInvalido = true
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
)

class ContratacaoFamilia(
    val contratante: Pessoa,
    val membros: MutableList<MembroFamilia>
)


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

    fun mostrarPerguntaFazerOutraContratacao() {
        println()
        println("Outra pessoa deseja fazer uma contratação?")
        println("1 - Sim, cadastrar outra pessoa")
        println("2 - Não, encerrar")
        print("Opção: ")
    }

    fun mostrarOpcaoInvalida() {
        println()
        println("Opção inválida, tente novamente!")
    }

    fun mostrarEntradaInvalida() {
        println()
        println("Entrada inválida. Digite apenas números.")
        print("Digite novamente: ")
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

    fun mostrarPerguntaTipoAssinatura() {
        println()
        println("A assinatura será qual tipo?")
        println("1 - Individual")
        println("2 - Familia")
        print("Opção: ")

    }

    fun mostrarTituloAssinaturaFamilia() {
        println()
        println("=== ASSINATURA FAMÍLIA ===")
        println("Vamos montar a assinatura começando por você e depois adicionar os dependentes.")
    }

    fun mostrarPerguntaAdicionarDependente() {
        println()
        println("Gostaria de adicionar outro dependente na assinatura família?")
        println("1 - Sim")
        println("2 - Não")
        print("Opção: ")
    }

    fun mostrarRelatorioGeral(
        pessoas: MutableList<Pessoa>,
        contratacoes: MutableList<Contratacao>,
        contratacoesFamilia: MutableList<ContratacaoFamilia>
    ) {
        println()
        println("=== RELATÓRIO GERAL ===")

        println()
        println("=== PESSOAS QUE NÃO CONSEGUIRAM CONTRATAR ===")

        if (pessoas.isEmpty()) {
            println("Lista vazia.")
        } else {
            for (pessoa in pessoas) {
                println()
                println("Nome: ${pessoa.nome}")
                println("Idade: ${pessoa.idade}")
                println("Não realizou contratação.")
                println("Motivo: menor de idade não pode contratar serviços.")
            }
        }

        println()
        println("=== ASSINATURAS INDIVIDUAIS ===")

        if (contratacoes.isEmpty()) {
            println("Lista vazia.")
        } else {
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

        println()
        println("=== ASSINATURAS FAMÍLIA ===")

        if (contratacoesFamilia.isEmpty()) {
            println("Lista vazia.")
        } else {
            for (familia in contratacoesFamilia) {
                println()
                println("Contratante: ${familia.contratante.nome}")
                println("Membros da família: ${familia.membros.size}")

                for (membro in familia.membros) {
                    println()

                    if (membro.pessoa.nome == familia.contratante.nome &&
                        membro.pessoa.idade == familia.contratante.idade
                    ) {
                        println("Membro (contratante): ${membro.pessoa.nome}")
                    } else {
                        println("Membro (dependente): ${membro.pessoa.nome}")
                    }

                    println("Idade: ${membro.pessoa.idade}")
                    println("Ofertas:")

                    for (resposta in membro.respostas) {
                        if (resposta.aceitou) {
                            println("${resposta.oferta.numero} - ${resposta.oferta.nome} - ACEITA")
                        } else {
                            println("${resposta.oferta.numero} - ${resposta.oferta.nome} - RECUSADA")
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