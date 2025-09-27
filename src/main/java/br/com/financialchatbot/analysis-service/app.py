from flask import Flask, request, jsonify, send_file
import pandas as pd
import matplotlib.pyplot as plt
import io

app = Flask(__name__)

# Dicionário FAKE para mapear tickers a setores
TICKER_SECTOR_MAP = {
    "PETR4": "Petróleo e Gás", "VALE3": "Mineração", "ITUB4": "Financeiro",
    "BBDC4": "Financeiro", "MGLU3": "Varejo", "LREN3": "Varejo",
    "WEGE3": "Industrial", "SUZB3": "Papel e Celulose"
}

@app.route('/analyze/diversification', methods=['POST'])
def analyze_diversification():
    portfolio_data = request.json
    assets = portfolio_data.get('assets', [])

    if not assets:
        return jsonify({"error": "No assets provided"}), 400

    # 1. Usa Pandas para processar os dados
    df = pd.DataFrame(assets)
    df['sector'] = df['ticker'].map(TICKER_SECTOR_MAP).fillna("Outros")

    # Simula o valor total de cada ativo (quantidade * preço)
    # A API da Brapi não fornece o valor total, então vamos usar o preço médio para o cálculo
    df['totalValue'] = df['quantity'] * df['averagePrice']

    diversification = df.groupby('sector')['totalValue'].sum()

    # 2. Usa Matplotlib para gerar o gráfico de pizza
    plt.style.use('seaborn-v0_8-darkgrid')
    fig, ax = plt.subplots(figsize=(8, 6))
    ax.pie(diversification, labels=diversification.index, autopct='%1.1f%%', startangle=90)
    ax.axis('equal') # Garante que a pizza seja um círculo.
    ax.set_title('Diversificação da Carteira por Setor')

    # 3. Salva a imagem em um buffer de memória em vez de um arquivo
    buf = io.BytesIO()
    plt.savefig(buf, format='png', bbox_inches='tight')
    buf.seek(0)
    plt.close(fig)

    # 4. Retorna a imagem como uma resposta HTTP
    return send_file(buf, mimetype='image/png')

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5001, debug=True)