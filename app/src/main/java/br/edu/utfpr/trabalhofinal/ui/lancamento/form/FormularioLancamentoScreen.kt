package br.edu.utfpr.trabalhofinal.ui.lancamento.form

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.edu.utfpr.trabalhofinal.R
import br.edu.utfpr.trabalhofinal.data.TipoLancamentoEnum
import br.edu.utfpr.trabalhofinal.ui.lancamento.form.composables.ConfirmationDialog
import br.edu.utfpr.trabalhofinal.ui.lancamento.form.composables.FormCheckbox
import br.edu.utfpr.trabalhofinal.ui.lancamento.form.composables.FormDatePicker
import br.edu.utfpr.trabalhofinal.ui.lancamento.form.composables.FormRadioButton
import br.edu.utfpr.trabalhofinal.ui.lancamento.form.composables.FormTextField
import br.edu.utfpr.trabalhofinal.ui.theme.TrabalhoFinalTheme
import br.edu.utfpr.trabalhofinal.ui.utils.composables.Carregando
import br.edu.utfpr.trabalhofinal.ui.utils.composables.ErroAoCarregar
import br.edu.utfpr.trabalhofinal.utils.formatar
import br.edu.utfpr.trabalhofinal.utils.toLocalDate
import java.text.DecimalFormatSymbols
import java.time.LocalDate

val decimalSeparator = DecimalFormatSymbols.getInstance().decimalSeparator;

@Composable
fun FormularioLancamentoScreen(
    modifier: Modifier = Modifier,
    onVoltarPressed: () -> Unit,
    viewModel: FormularioLancamentoViewModel = viewModel(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    LaunchedEffect(viewModel.state.lancamentoPersistidaOuRemovida) {
        if (viewModel.state.lancamentoPersistidaOuRemovida) {
            onVoltarPressed()
        }
    }
    val context = LocalContext.current
    LaunchedEffect(snackbarHostState, viewModel.state.codigoMensagem) {
        viewModel.state.codigoMensagem
            .takeIf { it > 0 }
            ?.let {
                snackbarHostState.showSnackbar(context.getString(it))
                viewModel.onMensagemExibida()
            }
    }

    val contentModifier: Modifier = modifier.fillMaxSize();

    if(viewModel.state.mostrarDialogConfirmacao){
        ConfirmationDialog(
            text = stringResource(R.string.mensagem_confirmacao_remover_lancamento),
            title = stringResource(R.string.excluir_lancamento),
            onDismiss = viewModel::ocultarDialogConfirmacao,
            onConfirm = viewModel::removerLancamento
        );
    }

    if (viewModel.state.carregando) {
        Carregando(modifier = contentModifier)
    } else if (viewModel.state.erroAoCarregar) {
        ErroAoCarregar(
            modifier = contentModifier,
            onTryAgainPressed = viewModel::carregarLancamento
        )
    } else {
        Scaffold(
            modifier = contentModifier,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                AppBar(
                    lancamentoNovo = viewModel.state.lancamentoNovo,
                    processando = viewModel.state.salvando || viewModel.state.excluindo,
                    onVoltarPressed = onVoltarPressed,
                    onSalvarPressed = viewModel::salvarLancamento,
                    //onExcluirPressed = viewModel::removerLancamento
                    onExcluirPressed = viewModel::mostrarDialogConfirmacao
                )
            }
        ) { paddingValues ->
            FormContent(
                modifier = Modifier.padding(paddingValues),
                processando = viewModel.state.salvando || viewModel.state.excluindo,
                descricao = viewModel.state.descricao,
                data = viewModel.state.data,
                valor = viewModel.state.valor,
                paga = viewModel.state.paga,
                tipo = viewModel.state.tipo,
                onDescricaoAlterada = viewModel::onDescricaoAlterada,
                onDataAlterada = viewModel::onDataAlterada,
                onValorAlterado = viewModel::onValorAlterado,
                onStatusPagamentoAlterado = viewModel::onStatusPagamentoAlterado,
                onTipoAlterado = viewModel::onTipoAlterado
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBar(
    modifier: Modifier = Modifier,
    lancamentoNovo: Boolean,
    processando: Boolean,
    onVoltarPressed: () -> Unit,
    onSalvarPressed: () -> Unit,
    onExcluirPressed: () -> Unit
) {
    TopAppBar(
        modifier = modifier.fillMaxWidth(),
        title = { Text(if (lancamentoNovo) stringResource(R.string.novo_lancamento) else stringResource(R.string.editar_lancamento)) },
        navigationIcon = {
            IconButton(onClick = onVoltarPressed) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.voltar)
                )
            }
        },
        actions = {
            if (processando) { CircularProgressIndicator( modifier = Modifier
                .size(60.dp)
                .padding(all = 16.dp),strokeWidth = 2.dp)
            } else {
                if (!lancamentoNovo) {
                    IconButton(onClick = onExcluirPressed) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = stringResource(R.string.excluir)
                        )
                    }
                }
                IconButton(onClick = onSalvarPressed) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = stringResource(R.string.salvar)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors().copy(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
            navigationIconContentColor = MaterialTheme.colorScheme.primary,
            actionIconContentColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun AppBarPreview() {
    TrabalhoFinalTheme {
        AppBar(
            lancamentoNovo = true,
            processando = false,
            onVoltarPressed = {},
            onSalvarPressed = {},
            onExcluirPressed = {}
        )
    }
}

@Composable
private fun FormContent(
    modifier: Modifier = Modifier,
    processando: Boolean,
    descricao: CampoFormulario,
    data: CampoFormulario,
    valor: CampoFormulario,
    paga: CampoFormulario,
    tipo: CampoFormulario,
    onDescricaoAlterada: (String) -> Unit,
    onDataAlterada: (String) -> Unit,
    onValorAlterado: (String) -> Unit,
    onStatusPagamentoAlterado: (String) -> Unit,
    onTipoAlterado: (String) -> Unit
) {
    Column(modifier = modifier
        .padding(all = 16.dp)
        .imePadding()
        .verticalScroll(rememberScrollState()) ) {
        val formTextFieldModifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp);

        Row(verticalAlignment = Alignment.CenterVertically){
            Column {
                Icon(imageVector = Icons.AutoMirrored.Filled.Notes, contentDescription = "Descrição", modifier = Modifier.size(24.dp));
            }
            Column {
                FormTextField(
                    modifier = formTextFieldModifier,
                    label = stringResource(R.string.descricao),
                    value = descricao.valor,
                    errorMessageCode = descricao.codigoMensagemErro,
                    onValueChanged = onDescricaoAlterada,
                    keyboardCapitalization = KeyboardCapitalization.Words,
                    enabled = !processando
                );
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically){
            Column (){
                Icon(imageVector = Icons.Filled.AttachMoney, contentDescription = "Valor", modifier = Modifier.size(24.dp));
            }
            Column (){
                FormTextField(
                    modifier = formTextFieldModifier,
                    label = stringResource(R.string.valor),
                    value = valor.valor,
                    errorMessageCode = valor.codigoMensagemErro,
                    //onValueChanged = onValorAlterado,
                    onValueChanged = { input ->
                        //filtra o teclado em tempo real para impedir chars inválidos e troca o ponto por decimalSeparator do sistema
                        val normalized = input.replace('.', decimalSeparator);

                        val result = buildString {
                            var separatorUsed = false;
                            for (char in normalized) {
                                if ( char.isDigit() ){ append(char); }
                                else if (char == decimalSeparator && !separatorUsed){ append(char); separatorUsed = true; }
                            }
                        };
                        //val cleaned = normalized.filter {it.isDigit() || it == decimalSeparator};
                        onValorAlterado(result);
                    },
                    keyboardType = KeyboardType.Number,
                    enabled = !processando
                );
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically){
            Column {
                Spacer(Modifier.size(24.dp))
            }
            Column {
                FormDatePicker(
                    modifier = formTextFieldModifier,
                    label = stringResource(R.string.data),
                    value = LocalDate.now(),
                    errorMessageCode = data.codigoMensagemErro,
                    onValueChanged = {newValue -> onDataAlterada(newValue.formatar())},
                    enabled = !processando
                )
            }
        }




        val checkOptionsModifier = Modifier.padding(vertical = 8.dp);
        FormCheckbox(
            modifier = checkOptionsModifier,
            label = stringResource(R.string.paga),
            checked = paga.valor.toBoolean(),
            onCheckChanged = { newValue ->
                onStatusPagamentoAlterado(newValue.toString())
            },
            enabled = !processando
        );
        Row {
            FormRadioButton(
                modifier = checkOptionsModifier,
                value = TipoLancamentoEnum.DESPESA,
                groupValue = TipoLancamentoEnum.valueOf(tipo.valor),
                onValueChanged = { newValue ->
                    onTipoAlterado(newValue.toString())
                },
                label = stringResource(R.string.despesa),
                enabled = !processando
            )
            FormRadioButton(
                modifier = checkOptionsModifier,
                value = TipoLancamentoEnum.RECEITA,
                groupValue = TipoLancamentoEnum.valueOf(tipo.valor),
                onValueChanged = { newValue ->
                    onTipoAlterado(newValue.toString())
                },
                label = stringResource(R.string.receita),
                enabled = !processando
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun FormContentPreview() {
    TrabalhoFinalTheme {
        FormContent(
            processando = false,
            descricao = CampoFormulario(),
            data = CampoFormulario(),
            valor = CampoFormulario(),
            paga = CampoFormulario(),
            tipo = CampoFormulario(TipoLancamentoEnum.RECEITA.toString()),
            onDescricaoAlterada = {},
            onDataAlterada = {},
            onValorAlterado = {},
            onStatusPagamentoAlterado = {},
            onTipoAlterado = {}
        )
    }
}