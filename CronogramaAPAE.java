import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class CronogramaAPAE extends JFrame {

    // Lista principal que guardará todos os agendamentos
    private List<Agendamento> listaAgendamentos = new ArrayList<>();

    // Componentes da Interface
    private JComboBox<String> comboDia;
    private JComboBox<String> comboHorario; // Agora é uma caixa de seleção
    private JTextField txtAluno;
    private JComboBox<String> comboResponsavel; // Serve para professor ou atendente
    private JRadioButton rbAtendente;
    private JRadioButton rbProfessor;
    private JTabbedPane painelAbasResponsaveis;

    // Listas de nomes
    private final String[] listaAtendentes = {
            "FONO RENATA", "FONO THALIA", "FISIO LORENA", "FISIO DANI", 
            "PSICO CAMILA", "PSICO EDUARDA", "PSICO ALINE", "PSICOEDUCACIONAL TATIANE"
    };
    private final String[] listaProfessores = {
            "CRISLAINE", "JOLAINE", "SARA", "BARBARA", "IZABEL", "TISSIANE"
    };
    private final String[] listaHorarios = {
            "07:30", "08:00", "08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30"
    };

    public CronogramaAPAE() {
        // Configurações da Janela Principal
        setTitle("Sistema de Cronograma CAEE - APAE");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // 1. Criar o Painel Superior (Formulário de Cadastro)
        JPanel painelCadastro = criarPainelCadastro();
        add(painelCadastro, BorderLayout.NORTH);

        // 2. Criar o Painel Central (Tabelas)
        painelAbasResponsaveis = new JTabbedPane();
        add(painelAbasResponsaveis, BorderLayout.CENTER);

        // 3. Criar o Painel Inferior (Botão de Relatório)
        JPanel painelInferior = criarPainelInferior();
        add(painelInferior, BorderLayout.SOUTH);

        // Atualizar as tabelas inicialmente vazias
        atualizarTabelas();
    }

    private JPanel criarPainelCadastro() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(BorderFactory.createTitledBorder("Cadastrar Novo Horário"));
        painel.setBackground(new Color(240, 248, 255)); 
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Linha 1: Dia e Horário
        gbc.gridx = 0; gbc.gridy = 0;
        painel.add(new JLabel("Dia da Semana:"), gbc);
        comboDia = new JComboBox<>(new String[]{"SEGUNDA", "TERÇA", "QUARTA", "QUINTA", "SEXTA"});
        gbc.gridx = 1;
        painel.add(comboDia, gbc);

        gbc.gridx = 2;
        painel.add(new JLabel("Horário:"), gbc);
        comboHorario = new JComboBox<>(listaHorarios);
        gbc.gridx = 3;
        painel.add(comboHorario, gbc);

        // Linha 2: Aluno
        gbc.gridx = 0; gbc.gridy = 1;
        painel.add(new JLabel("Nome do Aluno:"), gbc);
        txtAluno = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridwidth = 3; // Ocupa mais espaço
        painel.add(txtAluno, gbc);
        gbc.gridwidth = 1; // Reseta

        // Linha 3: Tipo (Atendente/Professor) e Nome
        gbc.gridx = 0; gbc.gridy = 2;
        painel.add(new JLabel("Tipo de Profissional:"), gbc);
        
        // Painel para os RadioButtons
        JPanel painelTipo = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        painelTipo.setBackground(new Color(240, 248, 255));
        rbAtendente = new JRadioButton("Clínico (Atendente)", true);
        rbProfessor = new JRadioButton("Pedagógico (Professor)");
        rbAtendente.setBackground(new Color(240, 248, 255));
        rbProfessor.setBackground(new Color(240, 248, 255));
        
        ButtonGroup grupoTipo = new ButtonGroup();
        grupoTipo.add(rbAtendente);
        grupoTipo.add(rbProfessor);
        painelTipo.add(rbAtendente);
        painelTipo.add(rbProfessor);
        
        gbc.gridx = 1;
        painel.add(painelTipo, gbc);

        gbc.gridx = 2;
        painel.add(new JLabel("Responsável:"), gbc);
        comboResponsavel = new JComboBox<>(listaAtendentes);
        comboResponsavel.setEditable(true);
        gbc.gridx = 3;
        painel.add(comboResponsavel, gbc);

        // Eventos para mudar a lista quando trocar entre Atendente/Professor
        rbAtendente.addActionListener(e -> atualizarListaResponsaveis(listaAtendentes));
        rbProfessor.addActionListener(e -> atualizarListaResponsaveis(listaProfessores));

        // Linha 4: Botão Salvar
        JButton btnSalvar = new JButton("Adicionar à Tabela");
        btnSalvar.setBackground(new Color(46, 139, 87));
        btnSalvar.setForeground(Color.WHITE);
        btnSalvar.setFont(new Font("Arial", Font.BOLD, 14));
        
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        painel.add(btnSalvar, gbc);

        btnSalvar.addActionListener(e -> adicionarAgendamento());

        return painel;
    }

    private JPanel criarPainelInferior() {
        JPanel painel = new JPanel();
        painel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        
        JButton btnRelatorio = new JButton("Gerar Relatório da Tabela Atual");
        btnRelatorio.setBackground(new Color(70, 130, 180)); // Azul Steel
        btnRelatorio.setForeground(Color.WHITE);
        btnRelatorio.setFont(new Font("Arial", Font.BOLD, 13));
        
        btnRelatorio.addActionListener(e -> gerarRelatorio());
        
        painel.add(btnRelatorio);
        return painel;
    }

    private void atualizarListaResponsaveis(String[] novaLista) {
        comboResponsavel.removeAllItems();
        for (String item : novaLista) {
            comboResponsavel.addItem(item);
        }
    }

    private void adicionarAgendamento() {
        String dia = (String) comboDia.getSelectedItem();
        String horario = (String) comboHorario.getSelectedItem();
        String aluno = txtAluno.getText().trim().toUpperCase();
        String responsavel = (String) comboResponsavel.getSelectedItem();

        if (aluno.isEmpty() || responsavel == null || responsavel.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, preencha o nome do aluno e do responsável!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String tipo = rbAtendente.isSelected() ? "Atendimento" : "Professor";

        listaAgendamentos.add(new Agendamento(dia, horario, aluno, responsavel.toUpperCase(), tipo));
        
        txtAluno.setText("");
        txtAluno.requestFocus();
        atualizarTabelas();
    }

    private void atualizarTabelas() {
        painelAbasResponsaveis.removeAll();

        Map<String, List<Agendamento>> mapaPorResponsavel = new HashMap<>();
        
        for (Agendamento a : listaAgendamentos) {
            mapaPorResponsavel.putIfAbsent(a.getResponsavel(), new ArrayList<>());
            mapaPorResponsavel.get(a.getResponsavel()).add(a);
        }

        for (Map.Entry<String, List<Agendamento>> entry : mapaPorResponsavel.entrySet()) {
            String nomeResponsavel = entry.getKey();
            List<Agendamento> agendamentos = entry.getValue();

            // Ordena os agendamentos pelo horário (ordem alfabética funciona para 07:30, 08:00...)
            agendamentos.sort((a1, a2) -> a1.getHorario().compareTo(a2.getHorario()));

            String[] colunas = {"Dia da Semana", "Horário", "Aluno", "Tipo"};
            DefaultTableModel modeloTabela = new DefaultTableModel(colunas, 0);

            for (Agendamento ag : agendamentos) {
                modeloTabela.addRow(new Object[]{ag.getDia(), ag.getHorario(), ag.getAluno(), ag.getTipo()});
            }

            JTable tabela = new JTable(modeloTabela);
            tabela.setRowHeight(25);
            tabela.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
            
            JScrollPane scrollPane = new JScrollPane(tabela);
            scrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));

            painelAbasResponsaveis.addTab(nomeResponsavel, scrollPane);
        }
    }

    private void gerarRelatorio() {
        int indexAbaAtual = painelAbasResponsaveis.getSelectedIndex();
        
        if (indexAbaAtual == -1) {
            JOptionPane.showMessageDialog(this, "Não há nenhuma tabela para gerar relatório!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nomeResponsavel = painelAbasResponsaveis.getTitleAt(indexAbaAtual);
        JScrollPane scrollPane = (JScrollPane) painelAbasResponsaveis.getComponentAt(indexAbaAtual);
        JTable tabela = (JTable) scrollPane.getViewport().getView();

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Salvar Relatório de " + nomeResponsavel);
        fileChooser.setSelectedFile(new File("Cronograma_" + nomeResponsavel.replace(" ", "_") + ".txt"));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File arquivoParaSalvar = fileChooser.getSelectedFile();

            try (PrintWriter writer = new PrintWriter(new FileWriter(arquivoParaSalvar))) {
                writer.println("==================================================");
                writer.println("CRONOGRAMA DE ATENDIMENTO/AULAS - " + nomeResponsavel);
                writer.println("==================================================");
                writer.println("DIA\t\tHORÁRIO\t\tALUNO\t\t\tTIPO");
                writer.println("--------------------------------------------------");

                for (int linha = 0; linha < tabela.getRowCount(); linha++) {
                    String dia = tabela.getValueAt(linha, 0).toString();
                    String hora = tabela.getValueAt(linha, 1).toString();
                    String aluno = tabela.getValueAt(linha, 2).toString();
                    String tipo = tabela.getValueAt(linha, 3).toString();

                    // Formatação simples para o arquivo de texto ficar alinhado
                    writer.printf("%-15s %-10s %-30s %-15s%n", dia, hora, aluno, tipo);
                }

                writer.println("--------------------------------------------------");
                writer.println("Gerado pelo Sistema CAEE - APAE");
                
                JOptionPane.showMessageDialog(this, "Relatório salvo com sucesso em:\n" + arquivoParaSalvar.getAbsolutePath(), "Sucesso", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao salvar o arquivo: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {}

        SwingUtilities.invokeLater(() -> {
            new CronogramaAPAE().setVisible(true);
        });
    }
}

// Classe Modelo atualizada
class Agendamento {
    private String dia;
    private String horario;
    private String aluno;
    private String responsavel;
    private String tipo;

    public Agendamento(String dia, String horario, String aluno, String responsavel, String tipo) {
        this.dia = dia;
        this.horario = horario;
        this.aluno = aluno;
        this.responsavel = responsavel;
        this.tipo = tipo;
    }

    public String getDia() { return dia; }
    public String getHorario() { return horario; }
    public String getAluno() { return aluno; }
    public String getResponsavel() { return responsavel; }
    public String getTipo() { return tipo; }
}