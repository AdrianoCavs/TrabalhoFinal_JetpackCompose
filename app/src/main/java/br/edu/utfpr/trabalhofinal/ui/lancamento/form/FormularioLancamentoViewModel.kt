package br.edu.utfpr.trabalhofinal.ui.lancamento.form

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import br.edu.utfpr.trabalhofinal.R
import br.edu.utfpr.trabalhofinal.data.LancamentoDatasource
import br.edu.utfpr.trabalhofinal.data.TipoLancamentoEnum
import br.edu.utfpr.trabalhofinal.ui.Arguments
import br.edu.utfpr.trabalhofinal.utils.formatar
import br.edu.utfpr.trabalhofinal.utils.toLocalDate
import java.math.BigDecimal
import java.text.DecimalFormatSymbols
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class FormularioLancamentoViewModel( savedStateHandle: SavedStateHandle ) : ViewModel() {
    private val idLancamento: Int = savedStateHandle.get<String>(Arguments.ID_LANCAMENTO)?.toIntOrNull() ?: 0 ;
    var state: FormularioLancamentoState by mutableStateOf(FormularioLancamentoState(idLancamento = idLancamento))
        private set

    val decimalSeparator = DecimalFormatSymbols.getInstance().decimalSeparator;

    init {
        if (state.idLancamento > 0) {
            carregarLancamento();
        }
    }

    fun carregarLancamento() {
        state = state.copy( carregando = true, erroAoCarregar = false );
        val lancamento = LancamentoDatasource.instance.carregar(state.idLancamento);
        state = if (lancamento == null) {
            state.copy( carregando = false, erroAoCarregar = true );
        } else {
            state.copy(
                carregando = false,
                lancamento = lancamento,
                descricao = state.descricao.copy(valor = lancamento.descricao),
                data = state.data.copy(valor = lancamento.data.formatar()),
                valor = state.valor.copy(valor = lancamento.valor.toString().replace('.', decimalSeparator)),
                paga = state.paga.copy(valor = lancamento.paga.toString()),
                tipo = state.tipo.copy(valor = lancamento.tipo.name)
            )
        }
    }

    fun onDescricaoAlterada(novaDescricao: String) {
        if (state.descricao.valor != novaDescricao) {
            state = state.copy(
                descricao = state.descricao.copy(
                    valor = novaDescricao,
                    codigoMensagemErro = validarDescricao(novaDescricao)
                )
            );
        }
    }

    private fun validarDescricao(descricao: String): Int {
        return if (descricao.isBlank()) {
            R.string.descricao_obrigatoria;
        } else {
            0
        }
    }

    private fun validarValor(valor: String): Int{
        return if (valor.isBlank()){
            R.string.valor_obrigatorio;
        } else 0
    }

    fun onValorAlterado(novoValor: String) {
        if (state.valor.valor != novoValor) {
            state = state.copy(
                valor = state.valor.copy(
                    valor = novoValor,
                    codigoMensagemErro = validarValor(novoValor)
                )
            );
        }
    }

    fun onDataAlterada(novaData: String) {
//        val dataConvertida = novaData.toLocalDate();
//        val dataFormatada = dataConvertida.formatar();
        if (state.data.valor != novaData) {
            state = state.copy( data = state.data.copy(valor = novaData) )
        }
    }

    fun onStatusPagamentoAlterado(novoStatusPagamento: String) {
        if (state.paga.valor != novoStatusPagamento) {
            state = state.copy(
                paga = state.paga.copy(
                    valor = novoStatusPagamento
                )
            )
        }
    }

    fun onTipoAlterado(novoTipo: String) {
        if (state.tipo.valor != novoTipo) {
            state = state.copy(
                tipo = state.tipo.copy(
                    valor = novoTipo
                )
            )
        }
    }

    fun salvarLancamento() {
        if (formularioValido()) {
            state = state.copy( salvando = true );
            val lancamento = state.lancamento.copy(
                descricao = state.descricao.valor,
                data = state.data.valor.toLocalDate(),
                //data = LocalDate.parse(state.data.valor, DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                valor = BigDecimal(state.valor.valor.replace(',', '.')),
                paga = state.paga.valor.toBoolean(),
                tipo = TipoLancamentoEnum.valueOf(state.tipo.valor)
            );
            LancamentoDatasource.instance.salvar(lancamento);
            state = state.copy( salvando = false, lancamentoPersistidaOuRemovida = true );
        }
    }

    private fun formularioValido(): Boolean {
        state = state.copy(
            descricao = state.descricao.copy( codigoMensagemErro = validarDescricao(state.descricao.valor) ),
            valor = state.valor.copy( codigoMensagemErro = validarValor(state.valor.valor) )
        )
        return state.formularioValido
    }

    fun mostrarDialogConfirmacao() {
        state = state.copy(mostrarDialogConfirmacao = true)
    }

    fun ocultarDialogConfirmacao() {
        state = state.copy(mostrarDialogConfirmacao = false)
    }

    fun removerLancamento() {
        state = state.copy(
            excluindo = true,
        )
        LancamentoDatasource.instance.remover(state.lancamento)
        state = state.copy(
            excluindo = false,
            lancamentoPersistidaOuRemovida = true
        )
    }

    fun onMensagemExibida() {
        state = state.copy(codigoMensagem = 0)
    }
}