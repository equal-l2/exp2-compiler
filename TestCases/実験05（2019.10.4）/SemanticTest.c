// ������Ɋւ���\����́��Ӗ���̓e�X�g
//
// ident.java��semanticCheck���\�b�h�̂Ƃ���́A�����Ȃ��Ă���͂�
//
// public void semanticCheck(CParseContext pcx) throws FatalErrorException {
// 	this.setCType(CType.getCType(CType.XXXXX));					// 	this.setConstant(YYYYY);
// }

// (1) XXXXX �𐮐��^ T_int �ɁAYYYYY��false�ɂ���
//a=0;	// �����i�����R�[�h�����������ǂ������m�F�j
//*a=1;	// �s��
//a[3]=1;	// �s��
//a=&1;	// �s��
//a=0	// �\����̓G���[�i�Z�~�R�����Ȃ��j
//a 0;	// �\����̓G���[�i���Ȃ��j

// (2) XXXXX ���|�C���^�^ T_pint �ɁAYYYYY��false�ɂ���
//a=1;	// �s��
//a=&1;	// �����i�����R�[�h�����������ǂ������m�F�j
//*a=1;	// �����i�����R�[�h�����������ǂ������m�F�j
//*10=1;	// �\����̓G���[

//a=&1;*a=1;	// �����i�����������Ȃ�A�����Ƃ���͂ƃR�[�h�����ł��邱�Ƃ��m�F����j

// (3) XXXXX ��z��^ T_array�i�l�ɂ���Ă��̖��O�͈قȂ�j�ɁAYYYYY��false�ɂ���
//a=1;	// �s��
//a=a;	// �s���i�������Ƃ���ƁA�z��S�̂���������R�s�[����̂ł����H�j
//a[3]=1;	// �����i�����R�[�h�����������ǂ������m�F�j
//a[3=1;	// �\����̓G���[�i]�����ĂȂ��j
//a 3]=1;	// �\����̓G���[�i[���J���ĂȂ��c�u�����Ȃ��v�Ƃ����G���[�ɂȂ�͂��j

// (4) XXXXX�����^ T_int �ɁAYYYYY��true�ɂ��āi�萔�ɂ͑���ł��Ȃ����Ƃ̊m�F�j
//a=1;	// �s��
