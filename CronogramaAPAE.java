import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class CronogramaAPAE extends JFrame {

    private List<Agendamento> listaAgendamentos = new ArrayList<>();
    private Agendamento agendamentoEmEdicao = null;

    // Componentes de Cadastro
    private JComboBox<String> comboDia;
    private JComboBox<String> comboHorario;
    private JTextField txtAluno;
    private JComboBox<String> comboAtendente;
    private JComboBox<String> comboProfessor;
    private JButton btnSalvar;
    private JButton btnCancelarEdicao;

    // Componentes de Visualização (Abas Principais e Secundárias)
    private JTabbedPane painelPrincipalVisoes; // Aba Mestre (Dia, Atendente, Professor)
    private JTabbedPane abasDias;
    private JTabbedPane abasAtendentes;
    private JTabbedPane abasProfessores;

    // Listas de nomes e horários
    private final String[] listaAtendentes = {
            "NENHUM", "FONO RENATA", "FONO THALIA", "FISIO LORENA", "FISIO DANI", 
            "PSICO CAMILA", "PSICO EDUARDA", "PSICO ALINE", "PSICOEDUCACIONAL TATIANE", "PSICO STEFANY"
    };
    private final String[] listaProfessores = {
            "NENHUM", "CRISLAINE", "JOLAINE", "SARA", "BARBARA", "IZABEL", "TISSIANE", "SELI"
    };
    private final String[] listaHorarios = {
            "07:30", "08:00", "08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30"
    };
    private final String[] ordemDias = {"SEGUNDA", "TERÇA", "QUARTA", "QUINTA", "SEXTA"};

    public CronogramaAPAE() {
        setTitle("Sistema de Cronograma CAEE - APAE");
        setSize(1000, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // 1. Painel Superior (Cadastro)
        add(criarPainelCadastro(), BorderLayout.NORTH);
        
        // 2. Painel Central (Modo de Visualização por Abas)
        painelPrincipalVisoes = new JTabbedPane();
        painelPrincipalVisoes.setFont(new Font("Arial", Font.BOLD, 14)); // Abas principais maiores
        
        abasDias = new JTabbedPane();
        abasAtendentes = new JTabbedPane();
        abasProfessores = new JTabbedPane();

        painelPrincipalVisoes.addTab("📅 Visão por Dia da Semana", abasDias);
        painelPrincipalVisoes.addTab("🩺 Visão por Atendente", abasAtendentes);
        painelPrincipalVisoes.addTab("📚 Visão por Professor", abasProfessores);
        
        // Dá um destaque visual para a área das tabelas
        JPanel painelTabelas = new JPanel(new BorderLayout());
        painelTabelas.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        painelTabelas.add(painelPrincipalVisoes, BorderLayout.CENTER);
        
        add(painelTabelas, BorderLayout.CENTER);
        
        // 3. Painel Inferior (Ações)
        add(criarPainelAcoes(), BorderLayout.SOUTH);

        atualizarTabelas();
    }

    private JPanel criarPainelCadastro() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(BorderFactory.createTitledBorder("Cadastrar / Editar Horário"));
        painel.setBackground(new Color(240, 248, 255)); 
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        painel.add(new JLabel("Dia da Semana:"), gbc);
        comboDia = new JComboBox<>(ordemDias);
        gbc.gridx = 1; painel.add(comboDia, gbc);

        gbc.gridx = 2;
        painel.add(new JLabel("Horário:"), gbc);
        comboHorario = new JComboBox<>(listaHorarios);
        gbc.gridx = 3; painel.add(comboHorario, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        painel.add(new JLabel("Nome do Aluno:"), gbc);
        txtAluno = new JTextField(20);
        gbc.gridx = 1; gbc.gridwidth = 3;
        painel.add(txtAluno, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 2;
        painel.add(new JLabel("Atendente (Clínico):"), gbc);
        comboAtendente = new JComboBox<>(listaAtendentes);
        comboAtendente.setEditable(true);
        gbc.gridx = 1; painel.add(comboAtendente, gbc);

        gbc.gridx = 2;
        painel.add(new JLabel("Professor (Pedagógico):"), gbc);
        comboProfessor = new JComboBox<>(listaProfessores);
        comboProfessor.setEditable(true);
        gbc.gridx = 3; painel.add(comboProfessor, gbc);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelBotoes.setBackground(new Color(240, 248, 255));
        
        btnSalvar = new JButton("Adicionar à Tabela");
        btnSalvar.setBackground(new Color(46, 139, 87));
        btnSalvar.setForeground(Color.WHITE);
        btnSalvar.setFont(new Font("Arial", Font.BOLD, 14));
        btnSalvar.addActionListener(e -> salvarAgendamento());

        btnCancelarEdicao = new JButton("Cancelar Edição");
        btnCancelarEdicao.setBackground(new Color(178, 34, 34));
        btnCancelarEdicao.setForeground(Color.WHITE);
        btnCancelarEdicao.setVisible(false);
        btnCancelarEdicao.addActionListener(e -> limparFormulario());

        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnCancelarEdicao);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.CENTER;
        painel.add(painelBotoes, gbc);

        return painel;
    }

    private JPanel criarPainelAcoes() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        
        JButton btnEditar = new JButton("✏️ Editar Selecionado");
        JButton btnExcluir = new JButton("❌ Excluir Selecionado");
        JButton btnRelatorio = new JButton("📄 Gerar Relatório da Tabela");

        btnEditar.addActionListener(e -> iniciarEdicao());
        btnExcluir.addActionListener(e -> excluirAgendamento());
        btnRelatorio.addActionListener(e -> gerarRelatorio());

        painel.add(btnEditar);
        painel.add(btnExcluir);
        painel.add(new JSeparator(SwingConstants.VERTICAL));
        painel.add(btnRelatorio);

        return painel;
    }

    private void salvarAgendamento() {
        String dia = (String) comboDia.getSelectedItem();
        String horario = (String) comboHorario.getSelectedItem();
        String aluno = txtAluno.getText().trim().toUpperCase();
        String atendente = (String) comboAtendente.getSelectedItem();
        String professor = (String) comboProfessor.getSelectedItem();

        if (aluno.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha o nome do aluno!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (atendente.equals("NENHUM") && professor.equals("NENHUM")) {
            JOptionPane.showMessageDialog(this, "Selecione ao menos um Atendente ou Professor!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (agendamentoEmEdicao == null) {
            listaAgendamentos.add(new Agendamento(dia, horario, aluno, atendente.toUpperCase(), professor.toUpperCase()));
        } else {
            agendamentoEmEdicao.setDia(dia);
            agendamentoEmEdicao.setHorario(horario);
            agendamentoEmEdicao.setAluno(aluno);
            agendamentoEmEdicao.setAtendente(atendente.toUpperCase());
            agendamentoEmEdicao.setProfessor(professor.toUpperCase());
        }

        limparFormulario();
        atualizarTabelas();
    }

    private void limparFormulario() {
        txtAluno.setText("");
        comboAtendente.setSelectedItem("NENHUM");
        comboProfessor.setSelectedItem("NENHUM");
        agendamentoEmEdicao = null;
        
        btnSalvar.setText("Adicionar à Tabela");
        btnSalvar.setBackground(new Color(46, 139, 87));
        btnCancelarEdicao.setVisible(false);
    }

    private void excluirAgendamento() {
        JTable tabelaAtual = obterTabelaAtual();
        if (tabelaAtual == null) return;

        int linhaSelecionada = tabelaAtual.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma linha na tabela para excluir.");
            return;
        }

        String idParaExcluir = tabelaAtual.getModel().getValueAt(linhaSelecionada, 0).toString();
        
        if (JOptionPane.showConfirmDialog(this, "Excluir este horário?", "Confirmação", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            listaAgendamentos.removeIf(a -> a.getId().equals(idParaExcluir));
            atualizarTabelas();
        }
    }

    private void iniciarEdicao() {
        JTable tabelaAtual = obterTabelaAtual();
        if (tabelaAtual == null) return;

        int linhaSelecionada = tabelaAtual.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma linha na tabela para editar.");
            return;
        }

        String idParaEditar = tabelaAtual.getModel().getValueAt(linhaSelecionada, 0).toString();
        
        for (Agendamento a : listaAgendamentos) {
            if (a.getId().equals(idParaEditar)) {
                agendamentoEmEdicao = a;
                break;
            }
        }

        if (agendamentoEmEdicao != null) {
            comboDia.setSelectedItem(agendamentoEmEdicao.getDia());
            comboHorario.setSelectedItem(agendamentoEmEdicao.getHorario());
            txtAluno.setText(agendamentoEmEdicao.getAluno());
            comboAtendente.setSelectedItem(agendamentoEmEdicao.getAtendente());
            comboProfessor.setSelectedItem(agendamentoEmEdicao.getProfessor());

            btnSalvar.setText("💾 Salvar Alterações");
            btnSalvar.setBackground(new Color(255, 140, 0));
            btnCancelarEdicao.setVisible(true);
        }
    }

    // Identifica qual aba mestre está aberta e pega a tabela da sub-aba
    private JTable obterTabelaAtual() {
        int visaoAtual = painelPrincipalVisoes.getSelectedIndex();
        JTabbedPane painelSecundarioAtivo;
        
        if (visaoAtual == 0) painelSecundarioAtivo = abasDias;
        else if (visaoAtual == 1) painelSecundarioAtivo = abasAtendentes;
        else painelSecundarioAtivo = abasProfessores;

        int indexAba = painelSecundarioAtivo.getSelectedIndex();
        if (indexAba == -1) return null; // Nenhuma aba aberta
        
        JScrollPane scrollPane = (JScrollPane) painelSecundarioAtivo.getComponentAt(indexAba);
        return (JTable) scrollPane.getViewport().getView();
    }

    // --- LÓGICA DE ORGANIZAÇÃO DAS TABELAS (SIMULTÂNEA) ---
    private void atualizarTabelas() {
        // Guarda as seleções atuais para não voltar pro início ao salvar
        int indexVisaoMain = painelPrincipalVisoes.getSelectedIndex();

        abasDias.removeAll();
        abasAtendentes.removeAll();
        abasProfessores.removeAll();

        Map<String, List<Agendamento>> mapaDias = new HashMap<>();
        Map<String, List<Agendamento>> mapaAtendentes = new HashMap<>();
        Map<String, List<Agendamento>> mapaProfessores = new HashMap<>();

        // 1. Separar os dados para cada visão
        for (Agendamento a : listaAgendamentos) {
            // Dias
            mapaDias.putIfAbsent(a.getDia(), new ArrayList<>());
            mapaDias.get(a.getDia()).add(a);
            
            // Atendentes
            if (!a.getAtendente().equals("NENHUM")) {
                mapaAtendentes.putIfAbsent(a.getAtendente(), new ArrayList<>());
                mapaAtendentes.get(a.getAtendente()).add(a);
            }
            
            // Professores
            if (!a.getProfessor().equals("NENHUM")) {
                mapaProfessores.putIfAbsent(a.getProfessor(), new ArrayList<>());
                mapaProfessores.get(a.getProfessor()).add(a);
            }
        }

        // 2. Preencher as sub-abas
        preencherAba(abasDias, mapaDias, Arrays.asList(ordemDias));
        
        List<String> ordemAtendentes = new ArrayList<>(mapaAtendentes.keySet());
        ordemAtendentes.sort(String::compareTo);
        preencherAba(abasAtendentes, mapaAtendentes, ordemAtendentes);

        List<String> ordemProfessores = new ArrayList<>(mapaProfessores.keySet());
        ordemProfessores.sort(String::compareTo);
        preencherAba(abasProfessores, mapaProfessores, ordemProfessores);
        
        // Restaura a aba mestre selecionada
        if (indexVisaoMain != -1) {
            painelPrincipalVisoes.setSelectedIndex(indexVisaoMain);
        }
    }

    // Método auxiliar para criar as tabelas dentro de uma Aba
    private void preencherAba(JTabbedPane painelAlvo, Map<String, List<Agendamento>> mapa, List<String> ordem) {
        for (String nomeAba : ordem) {
            if (!mapa.containsKey(nomeAba)) continue;

            List<Agendamento> agendamentos = mapa.get(nomeAba);
            agendamentos.sort((a1, a2) -> a1.getHorario().compareTo(a2.getHorario()));

            String[] colunas = {"ID", "Dia da Semana", "Horário", "Aluno", "Atendente", "Professor"};
            DefaultTableModel modeloTabela = new DefaultTableModel(colunas, 0) {
                @Override public boolean isCellEditable(int row, int column) { return false; }
            };

            for (Agendamento ag : agendamentos) {
                modeloTabela.addRow(new Object[]{ag.getId(), ag.getDia(), ag.getHorario(), ag.getAluno(), ag.getAtendente(), ag.getProfessor()});
            }

            JTable tabela = new JTable(modeloTabela);
            tabela.setRowHeight(25);
            
            // Oculta a coluna ID
            tabela.getColumnModel().getColumn(0).setMinWidth(0);
            tabela.getColumnModel().getColumn(0).setMaxWidth(0);
            tabela.getColumnModel().getColumn(0).setWidth(0);

            JScrollPane scrollPane = new JScrollPane(tabela);
            painelAlvo.addTab(nomeAba, scrollPane);
        }
    }

    private void gerarRelatorio() {
        int visaoAtual = painelPrincipalVisoes.getSelectedIndex();
        JTabbedPane painelSecundarioAtivo;
        
        if (visaoAtual == 0) painelSecundarioAtivo = abasDias;
        else if (visaoAtual == 1) painelSecundarioAtivo = abasAtendentes;
        else painelSecundarioAtivo = abasProfessores;

        if (painelSecundarioAtivo.getTabCount() == 0) {
            JOptionPane.showMessageDialog(this, "Não há tabela para gerar relatório!");
            return;
        }

        String nomeAba = painelSecundarioAtivo.getTitleAt(painelSecundarioAtivo.getSelectedIndex());
        JTable tabela = obterTabelaAtual();

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("Cronograma_" + nomeAba.replace(" ", "_") + ".txt"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(fileChooser.getSelectedFile()))) {
                writer.println("=========================================================================");
                writer.println("CRONOGRAMA - " + nomeAba);
                writer.println("=========================================================================");
                writer.printf("%-12s %-10s %-25s %-20s %-20s%n", "DIA", "HORÁRIO", "ALUNO", "ATENDENTE", "PROFESSOR");
                writer.println("-------------------------------------------------------------------------");

                for (int linha = 0; linha < tabela.getRowCount(); linha++) {
                    String dia = tabela.getModel().getValueAt(linha, 1).toString();
                    String hora = tabela.getModel().getValueAt(linha, 2).toString();
                    String aluno = tabela.getModel().getValueAt(linha, 3).toString();
                    String atendente = tabela.getModel().getValueAt(linha, 4).toString();
                    String prof = tabela.getModel().getValueAt(linha, 5).toString();

                    writer.printf("%-12s %-10s %-25s %-20s %-20s%n", dia, hora, aluno, atendente, prof);
                }
                JOptionPane.showMessageDialog(this, "Relatório salvo!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) { UIManager.setLookAndFeel(info.getClassName()); break; }
            }
        } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> new CronogramaAPAE().setVisible(true));
    }
}

class Agendamento {
    private String id;
    private String dia;
    private String horario;
    private String aluno;
    private String atendente;
    private String professor;

    public Agendamento(String dia, String horario, String aluno, String atendente, String professor) {
        this.id = UUID.randomUUID().toString();
        this.dia = dia;
        this.horario = horario;
        this.aluno = aluno;
        this.atendente = atendente;
        this.professor = professor;
    }

    public String getId() { return id; }
    public String getDia() { return dia; }
    public void setDia(String dia) { this.dia = dia; }
    public String getHorario() { return horario; }
    public void setHorario(String horario) { this.horario = horario; }
    public String getAluno() { return aluno; }
    public void setAluno(String aluno) { this.aluno = aluno; }
    public String getAtendente() { return atendente; }
    public void setAtendente(String atendente) { this.atendente = atendente; }
    public String getProfessor() { return professor; }
    public void setProfessor(String professor) { this.professor = professor; }
}